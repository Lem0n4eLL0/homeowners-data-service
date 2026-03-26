package ru.zeker.homeowners.domain.dto.response;

import java.util.UUID;
import ru.zeker.homeowners.domain.model.enums.MeterType;

public record MetersResponse(
    UUID id,
    String serialNumber,
    MeterType type

) {
}
