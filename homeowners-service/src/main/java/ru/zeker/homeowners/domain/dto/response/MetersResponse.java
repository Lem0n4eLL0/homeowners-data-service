package ru.zeker.homeowners.domain.dto.response;

import java.util.UUID;
import ru.zeker.homeowners.domain.model.enums.MeterType;
import ru.zeker.homeowners.domain.model.enums.ServiceCode;

public record MetersResponse(
    UUID id,
    String serialNumber,
    ServiceCode type

) {
}
