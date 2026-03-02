package ru.zeker.homeowners.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

@Schema(
        description = """
                Запрос на заполнение профиля и/или верификацию объекта недвижимости.
                        
                Правила валидации:
                - Для обновления профиля: заполнить firstName + lastName
                - Для привязки объекта: заполнить personalAccountNumber + city + street + houseNumber
                - Можно отправить обе группы сразу
                - surname, corpus, flatNumber — всегда опциональные
                """
)
public record UserProfileVerifyRequest(

        @Schema(
                description = "Имя пользователя",
                example = "Иван",
                maxLength = 100
        )
        @Size(max = 100, message = "Имя слишком длинное")
        String firstName,

        @Schema(
                description = "Фамилия пользователя",
                example = "Иванов",
                maxLength = 100
        )
        @Size(max = 100, message = "Фамилия слишком длинная")
        String lastName,

        @Schema(
                description = "Отчество пользователя",
                example = "Иванович",
                maxLength = 100
        )
        @Size(max = 100, message = "Отчество слишком длинное")
        String surname,

        @Schema(
                description = "Город проживания",
                example = "Москва",
                maxLength = 100
        )
        @Size(max = 100, message = "Название города слишком длинное")
        String city,

        @Schema(
                description = "Название улицы",
                example = "Ленина",
                maxLength = 200
        )
        @Size(max = 200, message = "Название улицы слишком длинное")
        String street,

        @Schema(
                description = "Номер дома",
                example = "10А",
                maxLength = 20
        )
        @Size(max = 20, message = "Номер дома слишком длинный")
        String houseNumber,

        @Schema(
                description = "Номер корпуса",
                example = "2",
                maxLength = 20
        )
        @Size(max = 20, message = "Номер корпуса слишком длинный")
        String corpus,

        @Schema(
                description = "Номер квартиры",
                example = "45",
                maxLength = 20
        )
        @Size(max = 20, message = "Номер квартиры слишком длинный")
        String flatNumber,

        @Schema(
                description = "Лицевой счет пользователя (только цифры)",
                example = "123456789012",
                maxLength = 50
        )
        @Size(max = 50, message = "Лицевой счет слишком длинный")
        @Pattern(regexp = "\\d+", message = "Лицевой счет должен содержать только цифры")
        String personalAccountNumber
) {

    @AssertTrue(message = "Нужно заполнить либо профиль полностью, либо объект полностью")
    public boolean isValidRequest() {
        return hasCompleteProfileData() || hasCompletePropertyData();
    }

    public boolean hasCompleteProfileData() {
        return StringUtils.isNotBlank(firstName) &&
                StringUtils.isNotBlank(lastName);
    }

    public boolean hasCompletePropertyData() {
        return StringUtils.isNotBlank(personalAccountNumber) &&
                StringUtils.isNotBlank(city) &&
                StringUtils.isNotBlank(street) &&
                StringUtils.isNotBlank(houseNumber);
    }
}