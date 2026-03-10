package ru.zeker.application.domain.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.zeker.application.domain.model.enums.Status;

import java.util.UUID;

/**
 * Запрос на заказ дополнительной услуги.
 */
@Schema(description = "Данные для заказа дополнительной услуги для объекта недвижимости")
public record OrderAdditionalServiceRequest(

        @Schema(
                description = "Уникальный идентификатор объекта недвижимости",
                example = "3c49f033-7242-4705-b643-d815334c9e4b",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID propertyId,

        @Schema(
                description = "Уникальный идентификатор услуги из каталога",
                example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID additionalServiceId,

        @Schema(
                description = "Статус заказа (по умолчанию: PENDING)",
                example = "PENDING",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                allowableValues = {"PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED"}
        )
        Status status
) {
    public OrderAdditionalServiceRequest {
        if (status == null) {
            status = Status.PENDING;
        }
    }
}