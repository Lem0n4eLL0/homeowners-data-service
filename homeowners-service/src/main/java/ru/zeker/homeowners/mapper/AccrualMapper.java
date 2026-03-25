package ru.zeker.homeowners.mapper;

import org.springframework.stereotype.Service;
import ru.zeker.homeowners.domain.dto.response.AccrualResponse;
import ru.zeker.homeowners.domain.dto.response.PeriodResponse;
import ru.zeker.homeowners.domain.dto.response.ServiceResponse;
import ru.zeker.homeowners.domain.model.entity.Accrual;

@Service
public class AccrualMapper {

    public AccrualResponse toResponse(Accrual accrual) {
        var services = accrual.getPersonalAccount().getPersonalAccountServices().stream()
                .map(pas -> {
                    var service = pas.getService();
                    return new ServiceResponse(service.getCode(), service.getName());
                })
                .toList();

        return AccrualResponse.builder()
                .id(accrual.getId())
                .propertyId(accrual.getPersonalAccount().getProperty().getId())
                .services(services)
                .servicesDetails(accrual.getServicesDetails())
                .period(new PeriodResponse(
                        accrual.getPeriod().lower(),
                        accrual.getPeriod().upper()
                ))
                .totalSum(accrual.getTotalSum())
                .paidAmount(accrual.getPaidAmount())
                .paidStatus(accrual.getPaidStatus())
                .createdAt(accrual.getCreatedAt())
                .build();
    }
}
