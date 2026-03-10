package ru.zeker.application.domain.model.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO для данных профиля пользователя.

 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalDataDto {

    @JsonProperty("personalDataId")
    @Schema(
            description = "Уникальный идентификатор профиля",
            example = "f7d73428-ad95-4ccb-bb3f-62ef10c25ca6",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UUID personalDataId;

    @JsonProperty("firstName")
    @Schema(
            description = "Имя пользователя",
            example = "Иван",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String firstName;

    @JsonProperty("lastName")
    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String lastName;

    @JsonProperty("surname")
    @Schema(
            description = "Отчество пользователя",
            example = "Иванович",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String surname;

    @Schema(
            description = "Список привязанных объектов недвижимости",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("properties")
    private List<UserPropertyDto> properties;


    /**
     * Конструктор для быстрого создания с основными полями.
     */
    public PersonalDataDto(UUID personalDataId, String firstName, String lastName) {
        this.personalDataId = personalDataId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.properties = List.of();  // Пустой список по умолчанию
    }

    /**
     * Добавить объект недвижимости в список.
     * Если список null — создаём новый.
     */
    public void addProperty(UserPropertyDto property) {
        if (this.properties == null) {
            this.properties = new ArrayList<>();
        }
        this.properties.add(property);
    }

    /**
     * Получить первый объект из списка (или null).
     */
    public UserPropertyDto getFirstProperty() {
        if (this.properties == null || this.properties.isEmpty()) {
            return null;
        }
        return this.properties.get(0);
    }

    /**
     * Проверить, есть ли объекты в списке.
     */
    public boolean hasProperties() {
        return this.properties != null && !this.properties.isEmpty();
    }
}