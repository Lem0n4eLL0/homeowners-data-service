package ru.zeker.homeowners.domain.dto.request;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание/обновление объекта недвижимости")
public record UserPropertyRequest(

        @Schema(description = "Название улицы", example = "Ленина", maxLength = 200)
        @NotNull(message = "Улица обязательна для заполнения")
        @Size(min = 1, max = 200, message = "Улица должна содержать от 1 до 200 символов")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9\\s-]+$",
                message = "Улица может содержать только буквы, цифры, пробелы и дефис")
        String street,

        @Schema(description = "Номер дома", example = "4Б", maxLength = 20)
        @NotNull(message = "Номер дома обязателен для заполнения")
        @Size(min = 1, max = 20, message = "Номер дома должен содержать от 1 до 20 символов")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9]+$",
                message = "Номер дома может содержать только буквы и цифры")
        String houseNumber,

        @Schema(description = "Номер корпуса", example = "2", maxLength = 20)
        @Nullable
        @Size(max = 20)
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9/]*$",
                message = "Номер корпуса может содержать только буквы, цифры и слеш")
        String corpus,

        @Schema(description = "Номер квартиры", example = "45", maxLength = 20)
        @NotNull(message = "Номер квартиры обязателен для заполнения")
        @Size(min = 1, max = 20, message = "Номер квартиры должен содержать от 1 до 20 цифр")
        @Pattern(regexp = "^\\d+$", message = "Номер квартиры должен содержать только цифры")
        String flatNumber,

        @Schema(description = "Лицевой счет пользователя (только цифры)", example = "1234567890", maxLength = 10)
        @NotNull(message = "Лицевой счет обязателен для заполнения")
        @Size(min = 10, max = 10, message = "Лицевой счет должен содержать ровно 10 цифр")
        @Pattern(regexp = "^\\d+$", message = "Лицевой счет должен содержать только цифры")
        String personalAccountNumber
) {}