package ru.zeker.homeowners.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Период начисления")
public record PeriodResponse(
        @Schema(description = "Начало периода", example = "2026-03-01")
        LocalDate start,

        @Schema(description = "Конец периода", example = "2026-03-31")
        LocalDate end
) {
}