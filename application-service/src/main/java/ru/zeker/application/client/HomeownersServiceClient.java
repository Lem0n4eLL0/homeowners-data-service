package ru.zeker.application.client;

import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;
import ru.zeker.application.domain.model.dto.external.PropertyDto;


import java.util.UUID;

@FeignClient(name = "homeowners-client", url = "http://homeowners-service:8082")
public interface HomeownersServiceClient {
//    @GetMapping("/property_membership/{id}")
//    public ResponseEntity<PropertyMembershipDto> getPersonalData(@PathVariable("id") UUID accountId);

    @GetMapping("/profile/me")
    public PersonalDataDto getPersonalData();

    @GetMapping("/property/{id}")
    public PropertyDto getProperty(@PathVariable("id") UUID id);




}
