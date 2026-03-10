

package ru.zeker.application.domain.model.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

/**
 * DTO для данных профиля пользователя.
 * <p>
 * Неизменяемый рекорд: для изменения полей используйте {@link #withProperties(List)}.
 */
@Schema(description = "Профиль пользователя: личные данные и список привязанных объектов недвижимости")
@JsonIgnoreProperties(ignoreUnknown = true)
public record PersonalDataDto(

        @Schema(
                description = "Уникальный идентификатор профиля",
                example = "f7d73428-ad95-4ccb-bb3f-62ef10c25ca6",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("personalDataId")
        UUID personalDataId,

        @Schema(
                description = "Имя пользователя",
                example = "Иван",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("firstName")
        String firstName,

        @Schema(
                description = "Фамилия пользователя",
                example = "Иванов",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("lastName")
        String lastName,

        @Schema(
                description = "Отчество пользователя",
                example = "Иванович",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("surname")
        String surname,

        @Schema(
                description = "Список привязанных объектов недвижимости",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("properties")
        List<UserPropertyDto> properties

) {
    /**
     * Компактный конструктор: гарантирует, что `properties` никогда не будет `null`.
     */
    public PersonalDataDto {
        if (properties == null) {
            properties = List.of();
        }
    }

    /**
     * Создать новый DTO с изменённым списком свойств (copy-with pattern).
     * <p>
     * Поскольку рекорд неизменяемый, этот метод возвращает новый экземпляр.
     *
     * @param newProperties новый список свойств
     * @return новый экземпляр {@link PersonalDataDto} с обновлёнными свойствами
     */
    public PersonalDataDto withProperties(List<UserPropertyDto> newProperties) {
        return new PersonalDataDto(
                this.personalDataId,
                this.firstName,
                this.lastName,
                this.surname,
                newProperties != null ? newProperties : List.of()
        );
    }



    /**
     * Проверить, есть ли объекты в списке.
     */
    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }
}