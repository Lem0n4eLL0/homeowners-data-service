package ru.zeker.application.domain.model.dto.response.application;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Краткая информация о дополнительной услуге (для списков).
 */
@Schema(description = "Краткая информация о дополнительной услуге для отображения в каталоге")
public record AdditionalServiceResponse(

        @Schema(
                description = "Уникальный идентификатор услуги",
                example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID additionalServiceId,

        @Schema(
                description = "Название услуги",
                example = "Генеральная уборка квартиры",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @Schema(
                description = "Базовая стоимость услуги в рублях",
                example = "2500",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int price
) {}