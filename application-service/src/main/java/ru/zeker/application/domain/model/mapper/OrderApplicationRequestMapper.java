package ru.zeker.application.domain.model.mapper;

import org.mapstruct.Mapper;
import ru.zeker.application.domain.model.dto.request.ApplicationRequest;
import ru.zeker.application.domain.model.dto.request.OrderAdditionalServiceRequest;
import ru.zeker.application.domain.model.entity.Application;
import ru.zeker.application.domain.model.entity.OrderAdditional;

@Mapper(componentModel = "spring")
public interface OrderApplicationRequestMapper {
    OrderAdditional toEntity(OrderAdditionalServiceRequest orderAdditionalServiceRequest);
    OrderAdditionalServiceRequest toModel(OrderAdditional entity);

}
