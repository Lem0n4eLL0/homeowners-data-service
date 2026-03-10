package ru.zeker.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.zeker.application.domain.model.dto.request.OrderAdditionalServiceRequest;
import ru.zeker.application.domain.model.dto.response.application.OrderAdditionalServiceResponse;
import ru.zeker.application.domain.model.entity.OrderAdditional;
import ru.zeker.application.domain.model.mapper.OrderApplicationRequestMapper;
import ru.zeker.application.domain.model.mapper.OrderApplicationResponseMapper;
import ru.zeker.application.exceptions.ServiceException;
import ru.zeker.application.repository.OrderAdditionalServiceRepository;
import ru.zeker.common.exception.ErrorCode;

import java.util.List;
import java.util.UUID;
@Slf4j
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
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(
                    "Ошибка валидации данных",
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.ERROR_VALIDATION
            );
        } catch (DataAccessException e) {
            log.error("Database error while creating application for accountId={}", accountId, e);
            throw new ServiceException(
                    "Не удалось создать заказ из-за ошибки базы данных",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.DATABASE_ERROR
            );
        }
        return responseMapper.toModel(orderAdditional);
    }

    public List<OrderAdditionalServiceResponse> getMyOrders(UUID accountId){
        List<OrderAdditional> orderAdditionals=repository.findAllByAccountId(accountId);
        return responseMapper.toModelList(orderAdditionals);
    }


}
