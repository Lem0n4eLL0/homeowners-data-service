package ru.zeker.application.domain.model.dto.response.application;

import java.util.List;
import java.util.UUID;

public record UserProfileDto(
    UUID id,
    String firstName,
    String lastName,
    String surname,
    String email,
    String phone,
    List<PropertyDto> properties
) {


}
