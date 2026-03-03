package ru.zeker.application.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;
import ru.zeker.application.domain.model.entity.Application;
import ru.zeker.application.service.ApplicationService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(
        name = "Application",
        description = "A controller for working with application"
)
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {
    ApplicationService applicationService;
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @RequestHeader("Account-Id") UUID accountId
    ) {
        List<ApplicationResponse> applications = applicationService.getByAccountId(accountId);
        return ResponseEntity.ok(applications);
    }
    @GetMapping("/me/{id}")
    public Application getApplication(){

    }
    @PostMapping
    public ResponseEntity<Application> createApplication(){

    }
}
