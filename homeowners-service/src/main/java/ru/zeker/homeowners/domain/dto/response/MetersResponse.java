package ru.zeker.homeowners.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import ru.zeker.homeowners.domain.model.enums.MeterType;
import ru.zeker.homeowners.domain.model.enums.ServiceCode;
@Schema(description = "Тело ответа счетчика")
public record MetersResponse(
    @Schema(description = "id объекта недвижимости", example = "d019f65b-5c55-4070-a4d3-9e946513e2d7")
    UUID id,
    @Schema(description = "id объекта недвижимости", example = "d019f65b-5c55-4070-a4d3-9e946513e2d7")
    String serialNumber,
    ServiceCode type

) {
}
