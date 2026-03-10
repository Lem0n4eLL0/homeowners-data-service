package ru.zeker.application.domain.model.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private UUID personalDataId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("surname")
    private String surname;

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