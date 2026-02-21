package ru.zeker.authentication.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.authentication.domain.dto.request.ConfirmationEmailRequest;
import ru.zeker.authentication.domain.dto.request.EmailRequest;
import ru.zeker.authentication.domain.dto.response.AccountResponse;
import ru.zeker.authentication.domain.dto.response.AuthenticationResponse;
import ru.zeker.authentication.domain.mapper.AccountMapper;
import ru.zeker.authentication.exception.AccountAlreadyEmailException;
import ru.zeker.authentication.exception.AccountEmailAlreadyUsedException;
import ru.zeker.authentication.exception.TooManyRequestsException;
import ru.zeker.authentication.service.AccountService;
import ru.zeker.authentication.service.AuthenticationService;

import java.util.UUID;

import static ru.zeker.common.headers.AppHeaders.ACCOUNT_ID;

@Validated
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Account",
        description = "Manage your account"
)
public class AccountController {

    private final AuthenticationService authenticationService;
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    /**
     * Retrieves information about the currently authenticated user.
     * <p>
     * Behavior:
     * 1. Uses the account ID from the request header.
     * 2. Returns account details including phone, email, and consent status.
     *
     * @param accountId unique identifier of the user (from header)
     * @return {@link AccountResponse} containing account details
     */
    @Operation(
            summary = "Get current account info",
            description = "Retrieves information about the currently authenticated user, including phone, email, and consent status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account information retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "Unique user identifier", hidden = true)
            @RequestHeader(ACCOUNT_ID) @NotNull UUID accountId
    ) {
        return ResponseEntity.ok(accountMapper.toResponse(accountService.findById(accountId)));
    }

    /**
     * Sends an email verification link to the specified email address.
     * <p>
     * Behavior:
     * 1. If the email is already confirmed for the current account, throws {@link AccountAlreadyEmailException}.
     * 2. If the email is used by another account, throws {@link AccountEmailAlreadyUsedException}.
     * 3. Enforces a cooldown period (default 60s) between sending verification emails to prevent spam.
     * 4. If all checks pass, generates a verification token and sends it via Kafka.
     * <p>
     * Always returns HTTP 202 to prevent user enumeration.
     *
     * @param accountId unique identifier of the user (from header)
     * @param request   {@link EmailRequest} containing email address
     * @return HTTP 202 (Accepted)
     * @throws AccountAlreadyEmailException     if email is already confirmed for this account
     * @throws AccountEmailAlreadyUsedException if email is already used by another account
     * @throws TooManyRequestsException         if email was requested too recently (cooldown)
     */
    @Operation(
            summary = "Request email verification",
            description = "Sends a verification email to the user if the email is not already confirmed or used by another account. Enforces 60s cooldown between requests."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Verification email sent"),
            @ApiResponse(responseCode = "400", description = "Invalid email format"),
            @ApiResponse(responseCode = "409", description = "Email already confirmed or already used by another account"),
            @ApiResponse(responseCode = "429", description = "Verification email requested too frequently")
    })
    @PostMapping("/email/request")
    public ResponseEntity<Void> requestEmailVerification(
            @Parameter(description = "Unique user identifier", hidden = true)
            @RequestHeader(ACCOUNT_ID) @NotNull UUID accountId,
            @RequestBody @Valid EmailRequest request
    ) {
        authenticationService.requestEmailVerification(request, accountId);
        return ResponseEntity.accepted().build();
    }

    /**
     * Confirms user's email address using a verification token.
     * <p>
     * On successful confirmation, issues new JWT tokens
     * and sets refresh token in HttpOnly cookie.
     *
     * @param request {@link ConfirmationEmailRequest} containing verification token
     * @return {@link AuthenticationResponse} containing access token
     */
    @Operation(
            summary = "Verify email",
            description = "Validates email verification token and issues new JWT tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Email confirmed successful",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    @PostMapping("/email/verify")
    public ResponseEntity<Void> confirmEmail(@RequestBody @Valid ConfirmationEmailRequest request) {
        authenticationService.confirmEmail(request);
        return ResponseEntity.noContent().build();
    }
}
