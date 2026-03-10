package ru.zeker.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zeker.application.domain.model.dto.request.ApplicationRequest;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceResponse;
import ru.zeker.application.domain.model.dto.response.application.ApplicationAllResponse;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;
import ru.zeker.application.domain.model.entity.Application;
import ru.zeker.application.service.ApplicationService;

import java.util.List;
import java.util.UUID;

import static ru.zeker.common.headers.AppHeaders.ACCOUNT_ID;

@Validated
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Application",
        description = "Контроллер для работы с заявками"
)
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {
    private final ApplicationService applicationService;
    @Operation(
            summary = "Получить список своих заявок пользователем ",
            description = "Возвращает информацию "
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список заявок успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdditionalServiceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content
            )
    })
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @RequestHeader("Account-Id") UUID accountId
    ) {
        log.info("Запрос на получение списка своих заявок пользователем");
        List<ApplicationResponse> applications = applicationService.getMyApplications(accountId);
        return ResponseEntity.ok(applications);
    }
    @Operation(
            summary = "Получить информацию о заявке пользователя ",
            description = "Возвращает информацию "
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Заявка получена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdditionalServiceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content
            )
    })

    @GetMapping("/my/{id}")
    public ResponseEntity<ApplicationAllResponse> getMyApplication(@PathVariable("id") UUID id,
                                                                    @RequestHeader(ACCOUNT_ID) UUID accountId
    ) {
        return ResponseEntity.ok(applicationService.getApplication(id,accountId));

    }
    @Operation(
            summary = "Получить список своих заявок пользователем ",
            description = "Возвращает информацию "
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Заявка создана",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdditionalServiceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity createApplication(@RequestBody @Valid ApplicationRequest application,
                                            @RequestHeader(ACCOUNT_ID) UUID accountId){
        log.info("Запрос на создание заявки");
        ApplicationResponse applicationResponse = applicationService.createApplication(application,accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationResponse);


    }



}
