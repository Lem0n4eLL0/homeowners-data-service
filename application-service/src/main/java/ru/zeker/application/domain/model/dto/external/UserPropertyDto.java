package ru.zeker.application.domain.model.dto.external;

import java.util.UUID;


public record UserPropertyDto(
        UUID propertyId,
        String city,
        String street,
        String houseNumber,
        String corpus,
        String flatNumber,
        String personalAccountNumber
) {}