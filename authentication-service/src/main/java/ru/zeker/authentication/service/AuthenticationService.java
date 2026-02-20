package ru.zeker.authentication.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.zeker.authentication.domain.dto.Tokens;
import ru.zeker.authentication.domain.dto.request.ConfirmationEmailRequest;
import ru.zeker.authentication.domain.dto.request.EmailRequest;
import ru.zeker.authentication.domain.dto.request.SmsRequest;
import ru.zeker.authentication.domain.dto.request.SmsVerifyRequest;
import ru.zeker.authentication.domain.dto.response.AccountExistsResponse;
import ru.zeker.authentication.domain.model.entity.Account;
import ru.zeker.authentication.exception.AccountAlreadyEmailException;
import ru.zeker.authentication.exception.AccountEmailAlreadyUsedException;
import ru.zeker.authentication.exception.InvalidTokenException;
import ru.zeker.authentication.exception.TooManyRequestsException;
import ru.zeker.authentication.security.SmsAuthenticationToken;
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
    private final AuthenticationManager authenticationManager;
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
     * Verifies the OTP code provided by the user, authenticates the user,
     * and issues JWT access and refresh tokens.
     *
     * @param request contains the user's phone and OTP code
     * @return Tokens object containing access and refresh JWTs
     * @throws BadCredentialsException if verification fails
     */
    public Tokens verifySmsCode(SmsVerifyRequest request) {
        var phone = request.getPhone();
        log.info("Attempting login via SMS for account={}", maskPhone(phone));

        var authentication = authenticationManager.authenticate(
                new SmsAuthenticationToken(phone, request.getCode())
        );

        var account = (Account) authentication.getPrincipal();
        log.debug("Authentication successful for account={}", maskPhone(phone));

        var jwtToken = jwtService.generateAccessToken(account);
        var tokens = Tokens.builder().token(jwtToken);

        if (account.isPersonalDataConsent()) {
            tokens.refreshToken(refreshTokenService.createRefreshToken(account));
        }

        log.info("User logged in successfully: account={}", maskPhone(phone));

        return tokens.build();
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
        user.setPersonalDataConsent(jwtUtils.getConsent(claims));

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

    @Transactional
    public Tokens acceptConsent(UUID accountId) {
        var account = accountService.findById(accountId);
        account.setPersonalDataConsent(true);
        accountService.update(account);
        var jwtToken = jwtService.generateAccessToken(account);
        var refreshToken = refreshTokenService.createRefreshToken(account);
        return Tokens.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
