package ru.zeker.homeowners.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на обновление информации профиля пользователя")
public record UserUpdateProfileRequest(

        @Schema(description = "Имя пользователя", example = "Иван", maxLength = 100)
        @NotNull(message = "Имя обязательно для заполнения")
        @Size(min = 1, max = 100, message = "Имя должно содержать от 1 до 100 символов")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ-]+$", message = "Имя может содержать только буквы и дефис")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Иванов", maxLength = 100)
        @NotNull(message = "Фамилия обязательна для заполнения")
        @Size(min = 1, max = 100, message = "Фамилия должна содержать от 1 до 100 символов")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ-]+$", message = "Фамилия может содержать только буквы и дефис")
        String lastName,

        @Schema(description = "Отчество пользователя", example = "Иванович", maxLength = 100)
        @Nullable
        @Size(max = 100, message = "Отчество должно содержать не более 100 символов")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ-]*$", message = "Отчество может содержать только буквы и дефис")
        String surname,

        @Schema(description = "Email пользователя (опционально)", example = "ivan@example.com", maxLength = 100)
        @Nullable
        @Email(message = "Email должен быть корректным")
        @Size(max = 100, message = "Email должен содержать не более 100 символов")
        String email
) {}