package ru.zeker.application.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import ru.zeker.application.client.AutentificationServiceClient;
import ru.zeker.application.client.HomeownersServiceClient;
import ru.zeker.application.domain.model.dto.external.ContactsDto;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;
import ru.zeker.application.domain.model.dto.external.PropertyDto;
import ru.zeker.application.domain.model.dto.request.ApplicationRequest;
import ru.zeker.application.domain.model.dto.response.application.ApplicationAllResponse;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;
import ru.zeker.application.domain.model.entity.Application;
import ru.zeker.application.domain.model.mapper.ApplicationMapper;
import ru.zeker.application.domain.model.mapper.ApplicationRequestMapper;
import ru.zeker.application.exceptions.ApplicationNotFoundedException;
import ru.zeker.application.repository.ApplicationRepository;

import java.util.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Slf4j
@Service
public class ApplicationService {
    ApplicationRepository repository;
    ApplicationMapper mapper;
    ApplicationRequestMapper requestMapper;
    HomeownersServiceClient client;
    AutentificationServiceClient authClient;
    public List<ApplicationResponse> getMyApplications(UUID accountId){
       return mapper.toModelList(repository.findAllByAccountId(accountId));

    }
    public ApplicationAllResponse getApplication(UUID applicationId,UUID accountId){
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundedException());

        PersonalDataDto personalDataDto=client.getPersonalData();
        PropertyDto propertyDto=client.getProperty(application.getPropertyId());
        ContactsDto contactsDto=authClient.getContacts();

        return  ApplicationAllResponse.toApplicationAllResponse(application,
                personalDataDto,
                propertyDto,
                contactsDto);

    }
    public ApplicationResponse createApplication(ApplicationRequest applicationRequest){
        Application application = requestMapper.toEntity(applicationRequest);
            Application saved = repository.save(application);
            try{
                 return mapper.toModel(saved);
            }catch(RuntimeException e){
                throw new RuntimeException("Ошибка при создании заявки");
            }





    }

}
