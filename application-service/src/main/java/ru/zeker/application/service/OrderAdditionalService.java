package ru.zeker.application.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zeker.application.domain.model.dto.request.OrderAdditionalServiceRequest;
import ru.zeker.application.domain.model.dto.response.application.OrderAdditionalServiceResponse;
import ru.zeker.application.domain.model.entity.OrderAdditional;
import ru.zeker.application.domain.model.mapper.OrderApplicationRequestMapper;
import ru.zeker.application.domain.model.mapper.OrderApplicationResponseMapper;
import ru.zeker.application.exceptions.ApplicationNotFoundedException;
import ru.zeker.application.repository.OrderAdditionalServiceRepository;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class OrderAdditionalService {

    private final OrderAdditionalServiceRepository repository;
    private final OrderApplicationRequestMapper mapper;
    private final OrderApplicationResponseMapper responseMapper;

    public OrderAdditionalServiceResponse createOrder(UUID accountId, OrderAdditionalServiceRequest order){
        OrderAdditional orderAdditional=mapper.toEntity(order);
        orderAdditional.setAccountId(accountId);

        try{
            repository.save(orderAdditional);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return responseMapper.toModel(orderAdditional);
    }

    public List<OrderAdditionalServiceResponse> getMyOrders(UUID accountId){
        List<OrderAdditional> orderAdditionals=repository.findAllByAccountId(accountId);
        return responseMapper.toModelList(orderAdditionals);
    }


}
