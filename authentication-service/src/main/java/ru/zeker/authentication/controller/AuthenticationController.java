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
import org.springframework.web.bind.annotation.*;
import ru.zeker.authentication.domain.dto.request.*;
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
            @ApiResponse(responseCode = "202", description = "SMS code sent"),
            @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/sms/request")
    public ResponseEntity<Void> requestSmsCode(
            @RequestBody @Valid SmsRequest request
    ) {
        authenticationService.requestSmsCode(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Completes passwordless authentication using SMS verification code.
     * <p>
     * If the account does not exist, it will be created automatically.
     * On success, returns JWT access token and sets refresh token in HttpOnly cookie.
     *
     * @param request  {@link SmsVerifyRequest} containing phone number and verification code
     * @param response HTTP response used to attach refresh token cookie
     * @return {@link AuthenticationResponse} containing access token
     */
    @Operation(
            summary = "Verify SMS code",
            description = "Validates the SMS code. Creates a new account if necessary and returns JWT tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired code")
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
     *
     * @param refreshToken refresh token from cookie
     * @param response     HTTP response used to attach new refresh token cookie
     * @return {@link AuthenticationResponse} containing new access token
     */
    @Operation(summary = "Refresh access token", description = "Refreshes access token using refresh token from cookie and returns new access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token successfully refreshed",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
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
