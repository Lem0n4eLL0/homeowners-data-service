package ru.zeker.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
/**
 * Контроллер для управления заявками пользователей.
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Просмотра списка заявок текущего пользователя</li>
 *   <li>Получения детальной информации о конкретной заявке</li>
 *   <li>Создания новой заявки на дополнительную услугу</li>
 * </ul>
 * <p>
 * <b>Требования аутентификации:</b>
 * <ul>
 *   <li>Все эндпоинты требуют заголовок {@code Account-Id} с валидным UUID</li>
 *   <li>Требуется JWT-токен в заголовке {@code Authorization}</li>
 *   <li>Заявки могут просматриваться только их владельцем</li>
 * </ul>
 *
 * @see ApplicationService
 * @see ApplicationResponse
 * @see ApplicationAllResponse
 */

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
            summary = "Получить список своих заявок",
            description = """
                    Возвращает список всех заявок по всем своим недвижимостям.
                                        
                    **Требования:**
                    - Заголовок `Account-Id` должен содержать валидный UUID
                    - Пользователь должен быть аутентифицирован
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список заявок успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Список пуст (нет заявок у пользователя)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный формат заголовка Account-Id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 400,
                                      "errorCode": "INVALID_UUID",
                                      "message": "Account-Id должен быть валидным UUID",
                                      "requestId": "abc-123"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 401,
                                      "errorCode": "UNAUTHORIZED",
                                      "message": "Требуется аутентификация",
                                      "requestId": "abc-123"
                                    }
                                    """)
                    )
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 500,
                                      "errorCode": "INTERNAL_SERVER_ERROR",
                                      "message": "An internal server error occurred. Please try again later",
                                      "requestId": "abc-123"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@Parameter(
            description = "Уникальный идентификатор аккаунта",
            required = true,
            in = ParameterIn.HEADER,
            example = "5edaa58c-3206-4f57-89e6-ea222f747348",
            schema = @Schema(type = "string", format = "uuid"))
            @RequestHeader("Account-Id") UUID accountId) {

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
    public ResponseEntity<ApplicationAllResponse> getMyApplication(
            @Parameter(
                    description = "Уникальный идентификатор заявки",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "77305b24-711a-4d18-bd8e-3eb2b93a53ab",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable("id") UUID id,
            @RequestHeader(ACCOUNT_ID) UUID accountId
    ) {
        return ResponseEntity.ok(applicationService.getApplication(id,accountId));

    }
    @Operation(
            summary = "Создать заявку"
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
