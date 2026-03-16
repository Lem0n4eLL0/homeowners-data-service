package ru.zeker.homeowners.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на обновление информации профиля пользователя")
public record UserUpdateProfileRequest(

        @Schema(description = "Имя пользователя", example = "Иван", maxLength = 100)
        @Nullable @Size(max = 100) String firstName,

        @Schema(description = "Фамилия пользователя", example = "Иванов", maxLength = 100)
        @Nullable @Size(max = 100) String lastName,

        @Schema(description = "Отчество пользователя", example = "Иванович", maxLength = 100)
        @Nullable @Size(max = 100) String surname,

        @Schema(description = "Email пользователя (опционально)", example = "ivan@example.com", maxLength = 100)
        @Nullable @Email(message = "Email должен быть корректным") @Size(max = 100) String email
) {}