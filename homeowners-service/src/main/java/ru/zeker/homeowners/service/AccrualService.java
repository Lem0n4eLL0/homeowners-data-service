package ru.zeker.homeowners.service;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.zeker.common.dto.kafka.homeowners.AccrualEvent;
import ru.zeker.homeowners.domain.dto.response.AccrualResponse;
import ru.zeker.homeowners.domain.model.entity.Accrual;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.mapper.AccrualMapper;
import ru.zeker.homeowners.repository.AccrualRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccrualService {

    private final AccrualRepository repository;
    private final AccrualMapper mapper;

    public Page<AccrualResponse> getAccruals(UUID accountId, Pageable pageable) {
        return repository.findAllByAccountId(accountId, pageable)
                .map(mapper::toResponse);
    }

    public Accrual createFromEvent(AccrualEvent event, PersonalAccount account) {

        if (MapUtils.isEmpty(event.servicesDetails())) {
            throw new IllegalArgumentException("servicesDetails must not be empty");
        }

        // Считаем сумму всех amount
        BigDecimal calculatedSum = event.servicesDetails().values().stream()
                .map(service -> {
                    if (Objects.isNull(service.getAmount())) {
                        throw new IllegalArgumentException("Service amount must not be null");
                    }
                    return service.getAmount();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Сравниваем с totalSum
        if (Objects.isNull(event.totalSum()) ||
                calculatedSum.compareTo(event.totalSum()) != 0) {

            throw new IllegalArgumentException(
                    "Total sum mismatch. Expected: " + event.totalSum() +
                            ", but calculated: " + calculatedSum
            );
        }

        return repository.save(
                Accrual.builder()
                        .personalAccount(account)
                        .period(Range.closed(event.periodStart(), event.periodEnd()))
                        .totalSum(event.totalSum())
                        .servicesDetails(event.servicesDetails())
                        .build()
        );
    }

}
