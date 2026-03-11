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
import ru.zeker.application.domain.model.dto.external.ContactsDto;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;
import ru.zeker.application.domain.model.dto.external.UserPropertyDto;
import ru.zeker.application.domain.model.dto.request.ApplicationRequest;
import ru.zeker.application.domain.model.dto.response.application.ApplicationAllResponse;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;
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
        try {
            return mapper.toModelList(repository.findAllByAccountId(accountId));
        } catch (DataAccessException e) {
            log.error("Database error while fetching applications for accountId={}", accountId, e);
            throw new ServiceException("Failed to fetch applications", HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.DATABASE_ERROR);
        }

    }

    public ApplicationAllResponse getApplication(UUID applicationId,UUID accountId){
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(applicationId));

        PersonalDataDto personalDataDto;
        try {
            log.info("Отправка запроса из application_service в homeowners_service для получения данных");
            personalDataDto = client.getPersonalData();

        } catch (FeignException e) {
            log.error("Failed to fetch personal data from homeowners-service: status={}, body={}",
                    e.status(), e.contentUTF8(), e);

            throw new ServiceException(
                    "Не удалось получить данные профиля",
                    HttpStatus.BAD_GATEWAY,
                    ErrorCode.EXTERNAL_SERVER_ERROR
            );
        }

        log.info("Поиск объекта недвижимости из списка недвижимостей, к которым относится заявка");
        UserPropertyDto userPropertyDto = personalDataDto.properties()!= null
                ? personalDataDto.properties().stream()
                .filter(p -> application.getPropertyId() != null &&
                        application.getPropertyId().equals(p.propertyId()))
                .findFirst()
                .orElse(null)
                : null;

        PersonalDataDto response=personalDataDto.withProperties(
                userPropertyDto != null ? List.of(userPropertyDto) : List.of()
        );



        ContactsDto contactsDto;
        try {
            contactsDto = authClient.getContacts();
        } catch (FeignException e) {
            log.error("Failed to fetch contacts from authentication-service: status={}", e.status(), e);
            throw new ServiceException(
                    "Не удалось получить контактные данные",
                    HttpStatus.BAD_GATEWAY,
                    ErrorCode.EXTERNAL_SERVER_ERROR
            );
        }

        return  ApplicationAllResponse.toApplicationAllResponse(application,
                List.of(personalDataDto),
                contactsDto);

    }
@Transactional
    public ApplicationResponse createApplication(ApplicationRequest applicationRequest,UUID accountId) {
        Application application = requestMapper.toEntity(applicationRequest);
        application.setAccountId(accountId);
        Application saved;
        try {
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
        return mapper.toModel(saved);
    }

}
