package ru.zeker.homeowners.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на заполнение профиля и привязку объекта недвижимости")
public record UserProfileVerifyRequest(

        @Schema(description = "Имя пользователя", example = "Иван", maxLength = 100)
        @NotBlank @Size(max = 100) String firstName,

        @Schema(description = "Фамилия пользователя", example = "Иванов", maxLength = 100)
        @NotBlank @Size(max = 100) String lastName,

        @Schema(description = "Отчество пользователя", example = "Иванович", maxLength = 100)
        @Size(max = 100) String surname,

        @Schema(description = "Название улицы", example = "Ленина", maxLength = 200)
        @NotBlank @Size(max = 200) String street,

        @Schema(description = "Номер дома", example = "10А", maxLength = 20)
        @NotBlank @Size(max = 20) String houseNumber,

        @Schema(description = "Номер корпуса", example = "2", maxLength = 20)
        @Size(max = 20) String corpus,

        @Schema(description = "Номер квартиры", example = "45", maxLength = 20)
        @NotBlank @Size(max = 20) String flatNumber,

        @Schema(description = "Лицевой счет пользователя (только цифры)", example = "123456789012", maxLength = 50)
        @NotBlank @Size(max = 50) @Pattern(regexp = "\\d+", message = "Лицевой счет должен содержать только цифры")
        String personalAccountNumber,

        @Schema(description = "Email пользователя (опционально)", example = "ivan@example.com", maxLength = 100)
        @Email(message = "Email должен быть корректным")
        String email
) {}