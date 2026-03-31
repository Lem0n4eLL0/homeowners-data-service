package ru.zeker.homeowners.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import ru.zeker.homeowners.domain.model.enums.MeterType;
import ru.zeker.homeowners.domain.model.enums.ServiceCode;
@Schema(description = "Запрос на добавление счетчика")
public record MeterRequest(
    @Schema(description = "id объекта недвижимости", example = "d019f65b-5c55-4070-a4d3-9e946513e2d7")
    @NotNull(message = "Personal account ID is required")
    UUID propertyId,
    @Schema(description = "Тип счетчика", example = "COLD_WATER")
    @NotNull(message = "Type meter's is required")
    ServiceCode type,
    @Schema(description = "Серийный номер счетчика", example = "WTR-2026-002143")
    @NotNull(message = "serial number is required")
    String serialNumber
) {

}
