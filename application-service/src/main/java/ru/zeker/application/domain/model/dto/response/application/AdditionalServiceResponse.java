package ru.zeker.application.domain.model.dto.response.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

public record AdditionalServiceResponse (
    UUID additionalServiceId,
    String title,
    int price
){}
