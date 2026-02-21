package ru.zeker.authentication.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User authentication request for passwordless login/registration via SMS")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsVerifyRequest {

    @Schema(description = "User phone number in E.164 format", example = "+79991234567", required = true)
    @NotBlank
    @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "Phone must be in E.164 format")
    private String phone;

    @Schema(description = "One-time SMS code", example = "666666", required = true)
    @NotBlank
    @Size(min = 6, max = 6, message = "Code must be 6 digits")
    @Pattern(regexp = "^\\d{6}$", message = "Code must contain only digits")
    private String code;

    @Schema(description = "User consent to personal data processing. Required only when creating a new account", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean personalDataConsent;
}