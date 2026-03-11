package ru.zeker.homeowners.domain.dto.response;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String firstName,
        String lastName,
        String surname,
        String email,
        String phone,
        List<UserPropertyResponse> properties
) {
}