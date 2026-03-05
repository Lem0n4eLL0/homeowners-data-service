package ru.zeker.application.domain.model.dto.request;

import ru.zeker.application.domain.model.enums.Status;

import java.util.UUID;

public record OrderAdditionalServiceRequest(
        UUID propertyId,
        UUID additionalServiceId,
        Status status

)
{
    public OrderAdditionalServiceRequest {
        if (status == null) {
            status = Status.PENDING;
        }
    }
}
