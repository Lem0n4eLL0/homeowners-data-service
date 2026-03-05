package ru.zeker.application.domain.model.dto.response.application;

import ru.zeker.application.domain.model.enums.Status;

import java.util.UUID;

public record OrderAdditionalServiceResponse(
        UUID id,
        UUID propertyId,
        UUID additionalServiceId,
        Status status
) {

}
