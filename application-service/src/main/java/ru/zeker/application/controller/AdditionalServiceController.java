package ru.zeker.application.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceInfoResponse;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceResponse;
import ru.zeker.application.service.AdditionalService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(
        name = "AdditionalServiceController",
        description = "A controller for working with additional services"
)
public class AdditionalServiceController {
    private final AdditionalService service;
    @GetMapping
    public ResponseEntity<List<AdditionalServiceResponse>> getAdditionalServices(){
        return ResponseEntity.ok(service.getAllAdditionalServices());
    }
    @GetMapping("/{id}")
    public ResponseEntity<AdditionalServiceInfoResponse> getAdditionalServices(@PathVariable("id") UUID additionalServiceId){
        return ResponseEntity.ok(service.getAdditionalService(additionalServiceId));
    }

}
