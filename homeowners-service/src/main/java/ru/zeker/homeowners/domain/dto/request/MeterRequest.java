package ru.zeker.homeowners.domain.dto.request;

import java.util.UUID;
import ru.zeker.homeowners.domain.model.enums.MeterType;

public record MeterRequest(
    UUID personalAccountId,
    MeterType type,
    String serialNumber
) {

}
