package ru.zeker.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.authentication.domain.dto.request.SmsRequest;
import ru.zeker.authentication.domain.dto.request.SmsVerifyRequest;
import ru.zeker.authentication.domain.dto.response.AccountExistsResponse;
import ru.zeker.authentication.domain.dto.response.AuthenticationResponse;
import ru.zeker.authentication.service.AuthenticationService;
import ru.zeker.authentication.service.RefreshTokenService;
import ru.zeker.authentication.util.CookieUtils;
import ru.zeker.common.config.JwtProperties;

import java.time.Duration;

/**
 * Controller responsible for passwordless authentication flow.
 * <p>
 * Provides:
 * - SMS-based authentication (request & verify code)
 * - Email verification
 * - JWT refresh handling
 * - Session termination (single device or all devices)
 * <p>
 * This controller does not implement classic username/password authentication.
 * User account is created automatically during successful SMS verification
 * if it does not already exist.
 */
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Passwordless authentication via SMS and email verification, JWT management and session control"
)
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    /**
     * Initiates passwordless authentication by sending an SMS verification code.
     * If an account with the provided phone number does not exist,
     * it will be created during successful verification step.
     *
     * @param request {@link SmsRequest} containing the user's phone number
     * @return HTTP 202 (Accepted) when the SMS request is successfully processed
     */
    @Operation(
            summary = "Request SMS verification code",
            description = "Sends a one-time verification code to the specified phone number to initiate passwordless authentication. " +
                    "If a code was recently sent, a 429 Too Many Requests is returned. Cooldown between requests is 60 seconds."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS code sent"),
            @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/sms/request")
    public ResponseEntity<AccountExistsResponse> requestSmsCode(
            @RequestBody @Valid SmsRequest request
    ) {
        return ResponseEntity.ok(authenticationService.requestSmsCode(request));
    }

    /**
     * Endpoint for passwordless authentication via SMS.
     * <p>
     * This endpoint validates the one-time SMS code sent to the user's phone.
     * The OTP has a limited lifetime and a limited number of verification attempts
     * (e.g. 5 failed attempts). After exceeding the maximum number of attempts,
     * the code becomes invalid and a new one must be requested.
     * <p>
     * If the account does not exist, a new account will be created only if the
     * {@code personalDataConsent} field is true. Otherwise, a {@code 400 Bad Request}
     * is returned indicating that consent is required.
     * <p>
     * On success, returns a JWT access token and sets a refresh token as an
     * HttpOnly cookie.
     * <p>
     * Flow:
     * 1. Verify OTP code.
     * 2. Enforce attempt limit.
     * 3. Authenticate existing user or create a new user with consent.
     * 4. Generate JWT access and refresh tokens.
     *
     * @param request  {@link SmsVerifyRequest} containing phone number, OTP code,
     *                 and optional {@code personalDataConsent} (required only for new users)
     * @param response HTTP response to attach refresh token cookie
     * @return {@link AuthenticationResponse} containing JWT access token
     */
    @Operation(
            summary = "Verify SMS code for passwordless login/registration",
            description = """
                        Validates the one-time SMS code.
                        The OTP has a limited lifetime and a maximum number of failed attempts (e.g. 5).
                        After exceeding the allowed number of attempts, the code becomes invalid
                        and a new one must be requested.
                        
                        Creates a new account if it does not exist and the user has given personal data consent.
                        Returns JWT tokens and sets the refresh token as an HttpOnly cookie.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP code or consent not provided for new account")
    })
    @PostMapping("/sms/verify")
    public ResponseEntity<AuthenticationResponse> verifySmsCode(
            @RequestBody @Valid SmsVerifyRequest request,
            HttpServletResponse response
    ) {
        var tokens = authenticationService.verifySmsCode(request);

        var cookie = CookieUtils.createTokenCookie(
                tokens.getRefreshToken(),
                Duration.ofMillis(jwtProperties.getRefresh().getExpiration())
        );
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new AuthenticationResponse(tokens.getToken()));
    }

    /**
     * Issues a new access token using a valid refresh token.
     * <p>
     * Refresh token is retrieved from HttpOnly cookie.
     * A new refresh token is generated and replaces the old one.
     * <p>
     * If the refresh token cookie is missing (e.g., user has not yet accepted personal data consent),
     * a 400 Bad Request is returned.
     *
     * @param refreshToken refresh token from cookie
     * @param response     HTTP response used to attach new refresh token cookie
     * @return {@link AuthenticationResponse} containing new access token
     */
    @Operation(
            summary = "Refresh access token",
            description = "Refreshes access token using refresh token from cookie and returns new access token. " +
                    "Returns 400 if the refresh token cookie is missing (e.g., consent not signed)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token successfully refreshed",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token cookie is missing", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @CookieValue(name = "refresh_token") @NotBlank String refreshToken,
            HttpServletResponse response) {
        var tokens = authenticationService.refreshToken(refreshToken);
        var cookie = CookieUtils.createTokenCookie(tokens.getRefreshToken(), Duration.ofMillis(jwtProperties.getRefresh().getExpiration()));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new AuthenticationResponse(tokens.getToken()));
    }

    /**
     * Terminates current session.
     * <p>
     * Revokes the provided refresh token and clears the cookie.
     *
     * @param refreshToken refresh token from cookie
     * @param response     HTTP response used to clear cookie
     * @return HTTP 204 (No Content)
     */
    @Operation(summary = "Logout from current session", description = "Revokes current refresh token and clears cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token", content = @Content)
    })
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token") @NotBlank String refreshToken,
            HttpServletResponse response) {
        refreshTokenService.revokeRefreshToken(refreshToken);
        var cookie = CookieUtils.createTokenCookie(StringUtils.EMPTY, Duration.ZERO);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }

    /**
     * Terminates all active sessions for the current user.
     * <p>
     * All refresh tokens associated with the user are revoked.
     * Current refresh token cookie is cleared.
     *
     * @param refreshToken refresh token from cookie
     * @param response     HTTP response used to clear cookie
     * @return HTTP 204 (No Content)
     */
    @Operation(summary = "Logout from all devices", description = "Revokes all user refresh tokens and clears cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All sessions terminated"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token", content = @Content)
    })
    @DeleteMapping("/logout/all")
    public ResponseEntity<Void> revokeAllRefreshTokens(
            @CookieValue(name = "refresh_token") @NotBlank String refreshToken,
            HttpServletResponse response) {
        refreshTokenService.revokeAllUserTokens(refreshToken);
        var cookie = CookieUtils.createTokenCookie(StringUtils.EMPTY, Duration.ZERO);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}
