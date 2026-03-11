package ru.zeker.authentication.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zeker.authentication.domain.dto.Tokens;
import ru.zeker.authentication.domain.dto.request.ConfirmationEmailRequest;
import ru.zeker.common.dto.request.EmailRequest;
import ru.zeker.authentication.domain.dto.request.SmsRequest;
import ru.zeker.authentication.domain.dto.request.SmsVerifyRequest;
import ru.zeker.authentication.domain.dto.response.AccountExistsResponse;
import ru.zeker.authentication.domain.model.entity.Account;
import ru.zeker.authentication.exception.AccountAlreadyEmailException;
import ru.zeker.authentication.exception.AccountEmailAlreadyUsedException;
import ru.zeker.authentication.exception.BadCredentialsException;
import ru.zeker.authentication.exception.ConsentRequiredException;
import ru.zeker.authentication.exception.InvalidTokenException;
import ru.zeker.authentication.exception.TooManyRequestsException;
import ru.zeker.common.dto.kafka.sms.SmsEvent;
import ru.zeker.common.dto.kafka.smtp.EmailEvent;
import ru.zeker.common.exception.ErrorCode;
import ru.zeker.common.util.JwtUtils;

import java.util.UUID;

import static ru.zeker.common.util.MaskPhoneUtils.maskPhone;

/**
 * Service for managing user authentication and registration
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AccountService accountService;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final KafkaProducer kafkaProducer;
    private final VerificationCooldownService verificationCooldownService;


    /**
     * Initiates the SMS verification process for a new or existing user.
     * Generates a one-time code, stores it temporarily, and publishes
     * a Kafka event to send an SMS to the user's phone.
     *
     * @param request contains the user's phone number
     */
    public AccountExistsResponse requestSmsCode(SmsRequest request) {
        var phone = request.getPhone();
        log.info("Requesting SMS verification for phone={}", maskPhone(phone));

        if (!otpService.canSend(phone)) {
            log.warn("Attempt to resend SMS too frequently for phone={}", maskPhone(phone));
            throw new TooManyRequestsException();
        }

        var code = otpService.generateAndStore(phone);

        var event = SmsEvent.builder()
                .phone(phone)
                .code(code)
                .build();
        kafkaProducer.sendSmsEvent(event);

        log.info("SMS verification event published for phone={}", maskPhone(phone));

        return new AccountExistsResponse(accountService.existsByPhone(phone));
    }

    /**
     * Verifies the OTP code provided by the user for passwordless authentication,
     * optionally creates a new account if it does not exist and the user has given
     * personal data consent, and issues JWT access and refresh tokens.
     * <p>
     * Security:
     * <ul>
     *     <li>OTP code has a limited lifetime (TTL).</li>
     *     <li>The number of failed verification attempts is limited (e.g. 5 attempts).</li>
     *     <li>After exceeding the maximum number of attempts, the OTP becomes invalid
     *     and a new code must be requested.</li>
     * </ul>
     * <p>
     * Flow:
     * 1. OTP code is verified via {@link OtpService}.
     * 2. If the number of failed attempts exceeds the allowed limit,
     *    {@link TooManyRequestsException} is thrown.
     * 3. Existing user → authenticated.
     * 4. New user → created only if {@code personalDataConsent = true}, otherwise
     *    {@link ConsentRequiredException} is thrown.
     * 5. JWT access token is generated.
     * 6. Refresh token is created and stored (e.g., in Redis) and returned to the client.
     *
     * @param request {@link SmsVerifyRequest} containing the phone number, OTP code,
     *                and optional personalDataConsent for new user registration.
     * @return {@link Tokens} containing JWT access and refresh tokens.
     * @throws BadCredentialsException  if OTP is invalid or expired
     * @throws TooManyRequestsException if maximum OTP verification attempts are exceeded
     * @throws ConsentRequiredException if the user does not exist and consent is not given
     */
    public Tokens verifySmsCode(SmsVerifyRequest request) {
        var phone = request.getPhone();
        log.info("Attempting login via SMS for account={}", maskPhone(phone));

        otpService.verify(phone, request.getCode());

        var account = accountService.loadByPhone(phone)
                .orElseGet(() -> {
                    if (!Boolean.TRUE.equals(request.getPersonalDataConsent())) {
                        throw new ConsentRequiredException();
                    }
                    return accountService.create(phone);
                });
        log.debug("Authentication successful for account={}", maskPhone(phone));

        var tokens = Tokens.builder()
                .token(jwtService.generateAccessToken(account))
                .refreshToken(refreshTokenService.createRefreshToken(account))
                .build();

        log.info("User logged in successfully: account={}", maskPhone(phone));

        return tokens;
    }


    /**
     * Refresh JWT token using refresh token
     *
     * @param refreshToken refresh token
     * @return new set of tokens
     */
    public Tokens refreshToken(String refreshToken) {
        log.debug("Token refresh request");

        var token = refreshTokenService.verifyRefreshToken(refreshToken);
        var claims = jwtUtils.extractAllClaims(refreshToken);
        var user = new Account();
        user.setId(UUID.fromString(jwtUtils.getAccountId(claims)));

        var jwtToken = jwtService.generateAccessToken(user);
        var newRefreshToken = refreshTokenService.rotateRefreshToken(token, user);

        log.debug("Tokens successfully refreshed for user: {}", user.getEmail());

        return Tokens.builder()
                .token(jwtToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void requestEmailVerification(@Valid EmailRequest request, UUID accountId) {
        log.info("Email verification request");

        var account = accountService.findById(accountId);
        var email = request.getEmail().toLowerCase();

        if (email.equals(account.getEmail())) {
            log.warn("Attempt to re-verify already confirmed email: {}", email);
            throw new AccountAlreadyEmailException();
        }

        if (accountService.existsByEmail(email)) {
            log.warn("Attempt to use an email already assigned to another account: {}", email);
            throw new AccountEmailAlreadyUsedException();
        }

        if (!verificationCooldownService.canResendEmail(email)) {
            log.warn("Attempt to resend verification email too frequently: {}", email);
            throw new TooManyRequestsException();
        }

        var token = jwtService.generateEmailToken(account, email);
        var event = EmailEvent.builder()
                .email(email)
                .token(token)
                .build();
        kafkaProducer.sendEmailEvent(event);

        verificationCooldownService.updateCooldown(email);
        log.info("Verification email sent to: {}", email);
    }

    /**
     * Confirm user email
     *
     * @param request request with JWT token for confirmation
     * @throws InvalidTokenException        if token is invalid
     * @throws AccountAlreadyEmailException if email is already confirmed
     */
    public void confirmEmail(ConfirmationEmailRequest request) {
        try {
            log.info("Email confirmation");
            var token = request.getToken();
            var claims = jwtUtils.extractAllClaims(token);
            var accountId = jwtUtils.getAccountId(claims);
            var email = jwtUtils.getEmail(claims);

            if (accountService.existsByEmail(email)) {
                log.warn("Attempt to re-confirm already email: {}", email);
                throw new AccountAlreadyEmailException();
            }

            var account = accountService.findById(UUID.fromString(accountId));
            if (!accountId.equals(account.getId().toString())) {
                log.warn("Attempt to confirm email with invalid token");
                throw new InvalidTokenException("Email confirmation token invalid", ErrorCode.INVALID_EMAIL_TOKEN);
            }

            account.setEmail(email);
            accountService.update(account);

            log.info("Email successfully confirmed for account: {}", email);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Email confirmation token expired", ErrorCode.EMAIL_TOKEN_EXPIRED);
        }
    }

}
