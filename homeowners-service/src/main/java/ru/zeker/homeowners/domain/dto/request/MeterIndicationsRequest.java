package ru.zeker.homeowners.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;


@Schema(description = "Запрос на передачу показания по счетчику")
public record MeterIndicationsRequest(

    @Schema(description = "id счетчика", example = "d019f65b-5c55-4070-a4d3-9e946513e2d7")
    @NotNull(message = "Meter ID is required")
    UUID meterId,

    @Schema(description = "Значение счетчика", example = "108439493")
    @NotNull(message = "Value is required")
    BigDecimal value

){

}
