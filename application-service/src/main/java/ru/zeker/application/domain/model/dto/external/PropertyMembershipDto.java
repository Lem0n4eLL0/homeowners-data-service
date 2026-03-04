package ru.zeker.application.domain.model.dto.external;

import java.util.UUID;

public record PropertyMembershipDto (
        UUID id,
        UUID userId,
        UUID propertyId

){
}
