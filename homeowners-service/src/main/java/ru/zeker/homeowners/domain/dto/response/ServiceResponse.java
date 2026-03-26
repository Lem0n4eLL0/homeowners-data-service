package ru.zeker.homeowners.domain.dto.response;

import ru.zeker.homeowners.domain.model.enums.ServiceCode;

public record ServiceResponse(
        ServiceCode code,
        String name) {
}
