// Файл: application-service/src/main/java/ru/zeker/application/domain/model/dto/external/UserPropertyDto.java

package ru.zeker.application.domain.model.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO для объекта недвижимости в контексте пользователя.
 */
@Schema(description = "Объект недвижимости, привязанный к профилю пользователя")
public record UserPropertyDto(

        @Schema(
                description = "Уникальный идентификатор объекта",
                example = "c250bf3d-b023-4016-a8f7-2fc82532bef8",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID propertyId,

        @Schema(
                description = "Город",
                example = "Москва",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String city,

        @Schema(
                description = "Название улицы",
                example = "Ленина",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String street,

        @Schema(
                description = "Номер дома",
                example = "10А",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String houseNumber,

        @Schema(
                description = "Номер корпуса/строения",
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
                description = "Лицевой счёт управляющей компании",
                example = "331545338424",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String personalAccountNumber
) {}