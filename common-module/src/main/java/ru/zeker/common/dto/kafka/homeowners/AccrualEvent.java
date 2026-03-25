package ru.zeker.common.dto.kafka.homeowners;

import ru.zeker.common.dto.model.ServiceDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AccrualEvent(
        String accountNumber,                // номер лицевого счета
        UUID companyId,                    // ID компании
        LocalDateTime periodStart,
        LocalDateTime periodEnd,
        BigDecimal totalSum,
        Map<String, ServiceDetails> servicesDetails // JSONB с детализацией
) {
}