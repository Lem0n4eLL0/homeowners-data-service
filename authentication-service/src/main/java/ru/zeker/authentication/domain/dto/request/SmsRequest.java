package ru.zeker.authentication.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "New user registration request")
public class SmsRequest {

    @Schema(description = "User phone number", example = "+79991234567", required = true)
    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "Phone must be in E.164 format (e.g. +79991234567)"
    )
    private String phone;
}