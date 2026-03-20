package ru.zeker.application.domain.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.zeker.application.domain.model.enums.Status;

import java.util.UUID;

import lombok.Builder;

/**
 * Запрос на создание заявки на дополнительную услугу.
 */
@Builder
@Schema(description = "Данные для создания новой заявки на дополнительную услугу")
public record ApplicationRequest(

        @Schema(
                description = "Уникальный идентификатор объекта недвижимости, для которого создаётся заявка",
                example = "3c49f033-7242-4705-b643-d815334c9e4b",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Property ID is required")
        UUID propertyId,

        @Schema(
                description = "Краткий заголовок заявки, описывающий суть проблемы",
                example = "Протечка в ванной",
                minLength = 3,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @Schema(
                description = "Подробное описание проблемы или комментария к заявке",
                example = "Капает кран, нужна замена уплотнителя",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        String comment,

        @Schema(
                description = "Статус заявки (по умолчанию: PENDING)",
                example = "PENDING",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"}
        )
        Status status
) {

}