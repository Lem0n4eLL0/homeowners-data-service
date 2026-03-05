package ru.zeker.homeowners.domain.dto.response;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String firstName,
        String lastName,
        String surname,
        List<UserPropertyLink> properties
) {
}