package ru.zeker.application.domain.model.dto.response.application;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.zeker.application.domain.model.enums.Status;

/**
 * Краткая информация о заявке (для списков).
 */
@Schema(description = "Краткая информация о заявке для отображения в списке")
public record ApplicationResponse(

        @Schema(
                description = "Заголовок заявки",
                example = "Протечка в ванной",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @Schema(
                description = "Текущий статус заявки",
                example = "PENDING",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"}
        )
        Status status
) {}