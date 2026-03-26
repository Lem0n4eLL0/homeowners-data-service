package ru.zeker.homeowners.domain.dto.response;

import io.hypersistence.utils.hibernate.type.range.Range;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.zeker.common.dto.model.ServiceDetails;
import ru.zeker.homeowners.domain.model.enums.PaidStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Schema(name = "AccrualResponse", description = "DTO начисления")
public record AccrualResponse(
        @Schema(description = "ID начисления", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "ID объекта недвижимости", example = "987e6543-e21b-12d3-a456-426614174000")
        UUID propertyId,

        @ArraySchema(schema = @Schema(implementation = ServiceResponse.class))
        List<ServiceResponse> services,

        @Schema(description = "Детали начислений по услугам")
        Map<String, ServiceDetails> servicesDetails,

        @Schema(description = "Период начисления")
        PeriodResponse period,

        @Schema(description = "Сумма начисления", example = "1500.50")
        BigDecimal totalSum,

        @Schema(description = "Оплаченная сумма", example = "500.50")
        BigDecimal paidAmount,

        @Schema(description = "Статус оплаты")
        PaidStatus paidStatus,

        @Schema(description = "Дата создания", example = "2026-03-25T10:15:30")
        LocalDateTime createdAt
) {
}
