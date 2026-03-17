

package ru.zeker.application.domain.model.dto.response.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для данных профиля пользователя.
 * <p>
 * }.
 */
@Schema(description = "Профиль пользователя: личные данные и список привязанных объектов недвижимости")
@JsonIgnoreProperties(ignoreUnknown = true)
public record PersonalDataDto(

//        @Schema(
//                description = "Уникальный идентификатор профиля",
//                example = "f7d73428-ad95-4ccb-bb3f-62ef10c25ca6",
//                requiredMode = Schema.RequiredMode.NOT_REQUIRED
//        )
//        @JsonProperty("personalDataId")
//        UUID personalDataId,

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
        String surname

) {





}