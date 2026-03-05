package ru.zeker.application.domain.model.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

public record PersonalDataDto(
        UUID personalDataId,
        String firstName,
        String lastName,
        String surname,
        UUID propertyId,
        String city,
        String corpus,
        String flatNumber,
        String houseNumber,
        String street
) {}
