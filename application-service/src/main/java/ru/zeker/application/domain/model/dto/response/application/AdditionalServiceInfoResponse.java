package ru.zeker.application.domain.model.dto.response.application;

import java.util.UUID;

public record AdditionalServiceInfoResponse (

        UUID additionalServiceId,
        String title,
        String description,
        int price
){}

