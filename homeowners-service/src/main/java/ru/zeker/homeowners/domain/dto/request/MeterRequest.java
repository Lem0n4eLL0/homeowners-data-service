package ru.zeker.homeowners.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import ru.zeker.homeowners.domain.model.enums.MeterType;
import ru.zeker.homeowners.domain.model.enums.ServiceCode;

public record MeterRequest(
    @NotNull(message = "Personal account ID is required")
    UUID propertyId,
    @NotNull(message = "Type meter's is required")
    ServiceCode type,
    @NotNull(message = "serial number is required")
    String serialNumber
) {

}
