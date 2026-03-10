package ru.zeker.application.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import ru.zeker.application.client.AutenticationServiceClient;
import ru.zeker.application.client.AutenticationServiceClient;
import ru.zeker.application.client.HomeownersServiceClient;
import ru.zeker.application.domain.model.dto.external.ContactsDto;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;
import ru.zeker.application.domain.model.dto.external.PropertyDto;
import ru.zeker.application.domain.model.dto.external.UserPropertyDto;
import ru.zeker.application.domain.model.dto.request.ApplicationRequest;
import ru.zeker.application.domain.model.dto.response.application.ApplicationAllResponse;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;
import ru.zeker.application.domain.model.entity.Application;
import ru.zeker.application.domain.model.mapper.ApplicationMapper;
import ru.zeker.application.domain.model.mapper.ApplicationRequestMapper;
import ru.zeker.application.exceptions.ApplicationNotFoundedException;
import ru.zeker.application.repository.ApplicationRepository;

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
       return mapper.toModelList(repository.findAllByAccountId(accountId));

    }

    public ApplicationAllResponse getApplication(UUID applicationId,UUID accountId){
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundedException());

        PersonalDataDto personalDataDto=client.getPersonalData();

        UserPropertyDto userPropertyDto = personalDataDto.getProperties() != null
                ? personalDataDto.getProperties().stream()
                .filter(p -> application.getPropertyId() != null &&
                        application.getPropertyId().equals(p.propertyId()))
                .findFirst()
                .orElse(null)
                : null;
        log.info("ПОЛУЧЕНО "+personalDataDto.getProperties().toString());


        personalDataDto.setProperties(userPropertyDto != null
                ? List.of(userPropertyDto)
                : List.of());


        ContactsDto contactsDto=authClient.getContacts();

        return  ApplicationAllResponse.toApplicationAllResponse(application,
                List.of(personalDataDto),
                contactsDto);

    }

    public ApplicationResponse createApplication(ApplicationRequest applicationRequest,UUID accountId){
        Application application = requestMapper.toEntity(applicationRequest);
        application.setAccountId(accountId);
            Application saved = repository.save(application);
            try{
                 return mapper.toModel(saved);
            }catch(RuntimeException e){
                throw new RuntimeException("Ошибка при создании заявки");
            }





    }

}
