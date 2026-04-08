package ru.zeker.application.service;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.atn.SemanticContext.OR;
import org.hibernate.query.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.zeker.application.client.HomeownersServiceClient;
import ru.zeker.application.domain.model.dto.request.OrderAdditionalServiceRequest;
import ru.zeker.application.domain.model.dto.response.application.OrderAdditionalServiceResponse;
import ru.zeker.application.domain.model.dto.response.application.PersonalDataDto;
import ru.zeker.application.domain.model.dto.response.application.PropertyDto;
import ru.zeker.application.domain.model.dto.response.application.UserProfileDto;
import ru.zeker.application.domain.model.entity.OrderAdditional;
import ru.zeker.application.domain.model.enums.Status;
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
  private final HomeownersServiceClient client;
  private final OrderApplicationResponseMapper responseMapper;

  public OrderAdditionalServiceResponse createOrder(UUID accountId,
      OrderAdditionalServiceRequest order) {
    UserProfileDto profile=client.getFullPersonalData(accountId);

    OrderAdditional orderAdditional = mapper.toEntity(order);
    orderAdditional.setAccountId(accountId);
    orderAdditional.setStatus(Status.PENDING);
    OrderAdditional saved;

    try {
      saved = repository.save(orderAdditional);
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
    PropertyDto property = PropertyService.getPropertyById(profile.properties(),orderAdditional.getPropertyId());
    PersonalDataDto personalData = PersonalDataDto.of(profile);
    OrderAdditionalServiceResponse response = OrderAdditionalServiceResponse.of(
        saved,
        property,
        personalData
    );
    return response;
  }

  public List<OrderAdditionalServiceResponse> getMyOrders(UUID accountId) {
    List<PropertyDto> properties = client.getFullPersonalData(accountId).properties();

    List<OrderAdditional> orders = new ArrayList<>();

    List<OrderAdditionalServiceResponse> response=new ArrayList<>();

    for(PropertyDto propertyDto:properties){
      orders.addAll(repository.findAllByPropertyId(propertyDto.propertyId()));
    }

    for(OrderAdditional order:orders){

      PersonalDataDto personalData=client.getPersonalData(order.getAccountId());

      PropertyDto property = PropertyService.getPropertyById(properties,order.getPropertyId());

      response.add( OrderAdditionalServiceResponse.of(
          order,
          property,
          personalData
      ));
    }

    return response;
  }


}
