package ru.zeker.application.domain.model.dto.response.application;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import ru.zeker.application.domain.model.entity.OrderAdditional;
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
            description = "Объект недвижимости",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        PropertyDto property,

        @Schema(
                description = "Уникальный идентификатор заказанной услуги",
                example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID additionalServiceId,
        @Schema(
            description = "Создатель заявки",
            example = "firstName: Иван\n"
                + "lastName: Иванов\n"
                + "surName:Иванович" ,

            requiredMode = Schema.RequiredMode.REQUIRED
        )
        PersonalDataDto personalDataDto,


        @Schema(
                description = "Статус заказа",
                example = "PENDING",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED"}
        )
        Status status,
        LocalDateTime createdAt
) {
        /**
         * Фабричный метод для создания DTO из сущности и дополнительных данных.
         * Это заменяет необходимость писать длинный new Record(...) в сервисе.
         *
         * @param entity Сущность заказа из БД
         * @param property DTO объекта недвижимости (уже заполненное)
         * @param creator DTO данных создателя (уже заполненное)
         * @return Готовый ответ для клиента
         */
        public static OrderAdditionalServiceResponse of(
            OrderAdditional entity,
            PropertyDto property,
            PersonalDataDto creator
        ) {
                return new OrderAdditionalServiceResponse(
                    entity.getId(),
                    property,
                    entity.getAdditionalServiceId(),
                    creator,
                    Status.PENDING,
                    entity.getCreatedAt()
                );
        }
}