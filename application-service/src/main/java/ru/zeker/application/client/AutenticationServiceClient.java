package ru.zeker.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.zeker.application.config.FeignConfig;
import ru.zeker.application.domain.model.dto.external.ContactsDto;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;

@FeignClient(
        name = "authentication-service",
        url = "${authentication.service.url:http://authentication-service:8080}",
        configuration = FeignConfig .class
)
public interface AutenticationServiceClient {
    @GetMapping("/accounts/me")
    public ContactsDto getContacts();
}
