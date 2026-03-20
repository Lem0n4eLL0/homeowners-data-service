package ru.zeker.application.service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.zeker.application.client.AutenticationServiceClient;
import ru.zeker.application.client.HomeownersServiceClient;
import ru.zeker.application.domain.model.dto.response.application.PersonalDataDto;
import ru.zeker.application.domain.model.dto.request.ApplicationRequest;
import ru.zeker.application.domain.model.dto.response.application.ApplicationAllResponse;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;
import ru.zeker.application.domain.model.dto.response.application.PropertyDto;
import ru.zeker.application.domain.model.dto.response.application.UserProfileDto;
import ru.zeker.application.domain.model.entity.Application;
import ru.zeker.application.domain.model.mapper.ApplicationMapper;
import ru.zeker.application.domain.model.mapper.ApplicationRequestMapper;
import ru.zeker.application.exceptions.ResourceNotFoundException;
import ru.zeker.application.exceptions.ServiceException;
import ru.zeker.application.repository.ApplicationRepository;
import ru.zeker.common.exception.ErrorCode;

import java.util.*;


@RequiredArgsConstructor
@Slf4j
@Service
public class ApplicationService {
    private final ApplicationRepository repository;

    private final ApplicationMapper mapper;
    private final ApplicationRequestMapper requestMapper;

    private final HomeownersServiceClient client;
    private final AutenticationServiceClient authClient;

    public List<ApplicationResponse> getMyApplications(UUID accountId){
        log.info("Получение объектов недвижимости пользователя для просмотра заявок по ним");

        UserProfileDto userProfile=client.getFullPersonalData(accountId);

        List<Application> applications = new ArrayList<>();

        List<ApplicationResponse> response=new ArrayList<>();

        log.info("Поиск заявок по propertyId");
        for(PropertyDto property: userProfile.properties()){
           applications.addAll(repository.findAllByPropertyId(property.propertyId()));
        }

        for(Application application:applications){

            log.info("Получение personaldata - информации о создателе заявки");
            PersonalDataDto personalData=client.getPersonalData(application.getAccountId());
            PropertyDto property = PropertyService.getPropertyById(userProfile.properties(),application.getPropertyId());


            log.info("Формирование ответа серверу");
            response.add(ApplicationResponse.of(application, property, personalData));

        }

        return response;


    }

    public ApplicationAllResponse getApplication(UUID applicationId,UUID accountId){

        log.info("Поиск заявки по id в бд");
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(applicationId));
        if(!application.getAccountId().equals(accountId)){
            throw new ResourceNotFoundException(applicationId);
        }

        PersonalDataDto personalDataDto;
        log.info("Отправка запроса из application_service в homeowners_service для получения данных");
        personalDataDto = client.getPersonalData(accountId);

        return  ApplicationAllResponse.toApplicationAllResponse(application,
                List.of(personalDataDto));

    }

@Transactional
    public ApplicationResponse createApplication(ApplicationRequest applicationRequest,UUID accountId) {


        Application application = requestMapper.toEntity(applicationRequest);

        application.setAccountId(accountId);

        Application saved;

        log.info("Получение данных о пользователе");

        UserProfileDto profile=client.getFullPersonalData(accountId);

        PropertyDto property = PropertyService.getPropertyById(profile.properties(),application.getPropertyId());

        PersonalDataDto personalData = PersonalDataDto.of(profile);

        try {
            log.info("Сохранение заявки в бд");
            saved = repository.save(application);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(
                    "Ошибка валидации данных",
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.ERROR_VALIDATION
            );
        } catch (DataAccessException e) {
            log.error("Database error while creating application for accountId={}", accountId, e);
            throw new ServiceException(
                    "Не удалось создать заявку из-за ошибки базы данных",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.DATABASE_ERROR
            );
        }
    ApplicationResponse response = ApplicationResponse.of(application, property, personalData);

        return response;
    }



}
