package ru.zeker.authentication.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request for sending email verification link")
public class EmailRequest {

    @Schema(
            description = "User email address",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
