package ru.zeker.homeowners.domain.dto.request;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на заполнение профиля и привязку объекта недвижимости")
public record UserProfileVerifyRequest(

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

        @Schema(description = "Название улицы", example = "Ленина", maxLength = 200)
        @NotNull(message = "Улица обязательна для заполнения")
        @Size(min = 1, max = 200, message = "Улица должна содержать от 1 до 200 символов")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9\\s-]+$", message = "Улица может содержать только буквы, цифры, пробелы и дефис")
        String street,

        @Schema(description = "Номер дома", example = "10А", maxLength = 20)
        @NotNull(message = "Номер дома обязателен для заполнения")
        @Size(min = 1, max = 20, message = "Номер дома должен содержать от 1 до 20 символов")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9]+$", message = "Номер дома может содержать только буквы и цифры")
        String houseNumber,

        @Schema(description = "Номер корпуса", example = "2", maxLength = 20)
        @Nullable
        @Size(max = 20, message = "Номер корпуса должен содержать не более 20 символов")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9\\s/]*$", message = "Номер корпуса может содержать только буквы, цифры, пробелы и слеш")
        String corpus,

        @Schema(description = "Номер квартиры", example = "45", maxLength = 20)
        @NotNull(message = "Номер квартиры обязателен для заполнения")
        @Size(min = 1, max = 20, message = "Номер квартиры должен содержать от 1 до 20 символов")
        @Pattern(regexp = "^\\d+$", message = "Номер квартиры должен содержать только цифры")
        String flatNumber,

        @Schema(description = "Лицевой счет пользователя (только цифры)", example = "123456789012", maxLength = 10)
        @NotNull(message = "Лицевой счет обязателен для заполнения")
        @Size(min = 10, max = 10, message = "Лицевой счет должен содержать ровно 10 цифр")
        @Pattern(regexp = "^\\d+$", message = "Лицевой счет должен содержать только цифры")
        String personalAccountNumber,

        @Schema(description = "Email пользователя (опционально)", example = "ivan@example.com", maxLength = 100)
        @Nullable
        @Email(message = "Email должен быть корректным")
        @Size(max = 100, message = "Email должен содержать не более 100 символов")
        String email
) {}