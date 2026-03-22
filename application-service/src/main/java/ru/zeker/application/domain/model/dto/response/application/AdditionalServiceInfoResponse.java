package ru.zeker.application.domain.model.dto.response.application;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Детальная информация о дополнительной услуге.
 */
@Schema(description = "Полная информация о дополнительной услуге: описание, цена, условия")
public record AdditionalServiceInfoResponse(

        @Schema(
                description = "Уникальный идентификатор услуги",
                example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID id,

        @Schema(
                description = "Название услуги",
                example = "Генеральная уборка квартиры",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @Schema(
                description = "Подробное описание услуги и условий оказания",
                example = "Включает влажную уборку всех помещений, мытьё окон, чистку сантехники",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String description,

        @Schema(
                description = "Стоимость услуги в рублях",
                example = "2500",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int price
) {}