package ru.zeker.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.zeker.application.domain.model.dto.external.ContactsDto;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;

@FeignClient(name = "homeowners-client", url = "http://authentication-service:8081")
public interface AutentificationServiceClient {
    @GetMapping("/personal_data/")
    public ContactsDto getContacts();
}
