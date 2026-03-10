// Файл: application-service/src/main/java/ru/zeker/application/domain/model/dto/external/ContactsDto.java

package ru.zeker.application.domain.model.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для контактных данных пользователя.
 */
@Schema(description = "Контактные данные пользователя: телефон и email")
public record ContactsDto(

        @Schema(
                description = "Номер телефона пользователя в международном формате",
                example = "+79966196352",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String phone,

        @Schema(
                description = "Электронная почта пользователя",
                example = "user@example.com",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String email
) {}