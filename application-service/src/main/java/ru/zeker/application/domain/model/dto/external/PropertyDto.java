package ru.zeker.application.domain.model.dto.external;

import java.util.UUID;

public record PropertyDto(
        UUID propertyId,
        String city,
        String corpus,
        String flatNumber,
        String houseNumber,
        String street


) {
}
