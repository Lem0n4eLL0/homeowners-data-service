package ru.zeker.application.domain.model.dto.response.application;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.zeker.application.domain.model.enums.Status;

import java.util.UUID;

/**
 * Информация о заказе дополнительной услуги.
 */
@Schema(description = "Данные заказа дополнительной услуги: идентификаторы и статус")
public record OrderAdditionalServiceResponse(

        @Schema(
                description = "Уникальный идентификатор заказа",
                example = "f430bf95-f9b2-44a0-8197-7baeff186ba5",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID id,

        @Schema(
                description = "Уникальный идентификатор объекта недвижимости",
                example = "3c49f033-7242-4705-b643-d815334c9e4b",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID propertyId,

        @Schema(
                description = "Уникальный идентификатор заказанной услуги",
                example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID additionalServiceId,

        @Schema(
                description = "Статус заказа",
                example = "PENDING",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED"}
        )
        Status status
) {}