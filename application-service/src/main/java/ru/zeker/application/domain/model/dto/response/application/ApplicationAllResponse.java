package ru.zeker.application.domain.model.dto.response.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import ru.zeker.application.domain.model.entity.Application;
import ru.zeker.application.domain.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Полная информация о заявке с данными владельца и объекта.
 */
@Builder
@Schema(description = "Детальная информация о заявке: статус, данные владельца, контакты, объект недвижимости")
public record ApplicationAllResponse(

        @Schema(
                description = "Уникальный идентификатор заявки",
                example = "77305b24-711a-4d18-bd8e-3eb2b93a53ab",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID applicationId,

        @Schema(
                description = "Заголовок заявки",
                example = "Протечка в ванной",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @Schema(
                description = "Подробный комментарий к заявке",
                example = "Капает кран, нужна замена уплотнителя",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String comment,

        @Schema(
                description = "Текущий статус заявки",
                example = "PENDING",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"}
        )
        Status status,

        @Schema(
                description = "Дата и время создания заявки",
                example = "2026-03-10T13:42:46.339487",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDateTime createdAt,

        @Schema(
                description = "Данные профиля владельца заявки",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        List<PersonalDataDto> personalDataDto

//        @Schema(
//                description = "Контактные данные владельца",
//                requiredMode = Schema.RequiredMode.NOT_REQUIRED
//        )
//        AccountResponse contactsDto

) {
    public static ApplicationAllResponse toApplicationAllResponse(
            Application application,
            List<PersonalDataDto> personalData) {

        return new ApplicationAllResponse(
                application.getId(),
                application.getTitle(),
                application.getComment(),
                application.getStatus(),
                application.getCreatedAt(),
                personalData);
    }
}