package ru.zeker.homeowners.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record MeterIndicationsRequest(
    @NotNull(message = "Meter ID is required")
    UUID meterId,
    @NotNull(message = "Value is required")
    BigDecimal value

){

}
