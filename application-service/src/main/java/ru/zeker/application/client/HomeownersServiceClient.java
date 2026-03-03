package ru.zeker.application.client;

import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;

import java.util.UUID;

@FeignClient(name = "homeowners-client", url = "http://homeowners-service:8082")
public interface HomeownersServiceClient {
    @GetMapping("/personal_data/{id}")
    public ResponseEntity<PersonalDataDto> getPersonalData(@PathVariable("id") UUID accountId);
//    @GetMapping("/property_memberships/{id}")
//    public ResponseEntity<> getPropertyMemberships(@PathVariable("id") UUID accountId);
}
