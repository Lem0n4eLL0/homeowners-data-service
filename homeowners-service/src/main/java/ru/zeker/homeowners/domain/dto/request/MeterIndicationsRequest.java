package ru.zeker.homeowners.domain.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record MeterIndicationsRequest(
    UUID meterId,
    BigDecimal value

){

}
