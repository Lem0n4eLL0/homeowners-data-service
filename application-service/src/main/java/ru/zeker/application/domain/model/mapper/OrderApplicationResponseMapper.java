package ru.zeker.application.domain.model.mapper;

import org.mapstruct.Mapper;
import ru.zeker.application.domain.model.dto.request.OrderAdditionalServiceRequest;
import ru.zeker.application.domain.model.dto.response.application.OrderAdditionalServiceResponse;
import ru.zeker.application.domain.model.entity.OrderAdditional;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderApplicationResponseMapper {
    OrderAdditional toEntity(OrderAdditionalServiceResponse orderAdditionalServiceResponse);
    OrderAdditionalServiceResponse toModel(OrderAdditional entity);

    List<OrderAdditional> toEntityList(List<OrderAdditionalServiceResponse> orderAdditionalServiceResponse);
    List<OrderAdditionalServiceResponse> toModelList(List<OrderAdditional> orderAdditionals);
}
