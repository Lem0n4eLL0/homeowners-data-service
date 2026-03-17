package ru.zeker.application.domain.model.dto.response.application;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import ru.zeker.application.domain.model.enums.Status;

/**
 * Краткая информация о заявке (для списков).
 */
@Schema(description = "Краткая информация о заявке для отображения в списке")
public record ApplicationResponse(

    @Schema(
        description = "id заявки",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    UUID id,
    @Schema(
        description = "id объекта недвижимости",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    UUID propertyId,
    @Schema(
        description = "Дата создания",
        example = "2023-10-27T14:30:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalDateTime createdAt,
    @Schema(
        description = "Создатель заявки",
        example = "firstName: Иван\n"
            + "lastName: Иванов\n"
            + "surName:Иванович" ,

        requiredMode = Schema.RequiredMode.REQUIRED
    )
    PersonalDataDto createdBy,

    @Schema(
        description = "Заголовок заявки",
        example = "Протечка в ванной",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String title,
    @Schema(
        description = "Комментарий к заявке",
        example = "Уберите 10 листьев и все желуди пожалуйста!!!!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String comment,

    @Schema(
                description = "Текущий статус заявки",
                example = "PENDING",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"}
        )
        Status status

) {}