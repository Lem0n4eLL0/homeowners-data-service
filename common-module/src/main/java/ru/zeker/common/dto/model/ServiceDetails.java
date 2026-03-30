package ru.zeker.common.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ServiceDetails", description = "Детали начисления по одной услуге")
public class ServiceDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Schema(description = "Сумма по услуге", required = true, example = "500.50")
    private BigDecimal amount;

    @Schema(description = "Объём услуги", example = "10.0")
    private BigDecimal volume;

    @Schema(description = "Единица измерения", example = "кВт·ч")
    private String unit;

    @Schema(description = "Тариф услуги", example = "50.05")
    private BigDecimal tariff;
}