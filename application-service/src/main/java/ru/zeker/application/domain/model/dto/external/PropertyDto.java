package ru.zeker.application.domain.model.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * DTO для данных объекта недвижимости.
 */
@Schema(description = "Информация об объекте недвижимости: адрес и идентификаторы")
public record PropertyDto(

        @Schema(
                description = "Уникальный идентификатор объекта",
                example = "c250bf3d-b023-4016-a8f7-2fc82532bef8",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID propertyId,

        @Schema(
                description = "Корпус/строение",
                example = "2",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String corpus,

        @Schema(
                description = "Номер квартиры",
                example = "101",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String flatNumber,

        @Schema(
                description = "Номер дома",
                example = "10А",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String houseNumber,

        @Schema(
                description = "Название улицы",
                example = "Ленина",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String street
) {}