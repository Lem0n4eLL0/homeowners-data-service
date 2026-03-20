package ru.zeker.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zeker.application.domain.model.dto.request.OrderAdditionalServiceRequest;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceInfoResponse;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceResponse;
import ru.zeker.application.domain.model.dto.response.application.OrderAdditionalServiceResponse;
import ru.zeker.application.service.OrderAdditionalService;

import java.util.List;
import java.util.UUID;

import static ru.zeker.common.headers.AppHeaders.ACCOUNT_ID;

/**
 * Контроллер для управления заказами дополнительных услуг.
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Оформления заказа на дополнительную услугу для объекта недвижимости</li>
 *   <li>Просмотра истории заказанных услуг текущего пользователя</li>
 * </ul>
 * <p>
 * <b>Требования аутентификации:</b>
 * <ul>
 *   <li>Все эндпоинты требуют заголовок {@code Account-Id} с валидным UUID</li>
 *   <li>Заказы могут создаваться и просматриваться только их владельцем</li>
 *   <li>Проверка прав осуществляется на уровне сервиса</li>
 * </ul>
 * <p>
 * <b>Бизнес-правила:</b>
 * <ul>
 *   <li>Услуга и объект недвижимости должны существовать в системе</li>
 *   <li>Объект должен быть привязан к аккаунту заказчика</li>
 *   <li>Повторный заказ на ту же услугу для того же объекта создаётся как новая запись</li>
 * </ul>
 *
 * @see OrderAdditionalService
 * @see OrderAdditionalServiceResponse
 * @see ru.zeker.application.domain.model.dto.request.OrderAdditionalServiceRequest
 */

@Slf4j
@Validated
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(
        name = "OrderAdditionalServiceController",
        description = "Контроллер для обработки заказов услуг"
)
public class OrderAdditionalServiceController {
    private final OrderAdditionalService service;
    @Operation(
            summary = "Заказать дополнительную услугу",
            description = """
                    Создаёт заказ на дополнительную услугу для объекта недвижимости.
                                        
                    **Требования к телу запроса:**
                    - `serviceId` — UUID услуги из каталога (обязательно)
                    - `propertyId` — UUID объекта недвижимости (обязательно)
                                        
                    **Бизнес-правила:**
                    - Услуга должна существовать в каталоге
                    - Объект должен быть привязан к аккаунту пользователя
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Услуга успешно заказана",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderAdditionalServiceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 400,
                                      "errorCode": "VALIDATION_FAILED",
                                      "message": "Parameter validation error",
                                      "details": {
                                        "serviceId": "Service ID is required",
                                        "propertyId": "Property ID must be a valid UUID"
                                      },
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
                    responseCode = "404",
                    description = "Ресурс не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 404,
                                      "errorCode": "RESOURCE_NOT_FOUND",
                                      "message": "Услуга или объект недвижимости не найдены",
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
    @PostMapping
    public ResponseEntity<OrderAdditionalServiceResponse> addAdditionalService(
            @Parameter(
                    description = "Данные для заказа услуги",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderAdditionalServiceRequest.class))
            )
            @RequestBody OrderAdditionalServiceRequest orderAdditionalServiceRequest,
            @Parameter(
                    description = "Уникальный идентификатор аккаунта",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "5edaa58c-3206-4f57-89e6-ea222f747348",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @RequestHeader(ACCOUNT_ID) UUID accountId){
        log.info("Запрос на создание заказа услуги");
        log.info("Заказ услуги: accountId={}, serviceId={}, propertyId={}",
                accountId,
                orderAdditionalServiceRequest.additionalServiceId(),
                orderAdditionalServiceRequest.propertyId());
        return ResponseEntity.ok(service.createOrder(accountId,orderAdditionalServiceRequest));

    }
    @Operation(
            summary = "Получить список заказанных услуг",
            description = """
                    Возвращает список всех заказанных дополнительных услуг по всем недвижимостям пользователя.
                                        
                    **Использование:**
                    - Отображение истории заказов в личном кабинете
                    - Проверка статуса выполнения услуг
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список услуг успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderAdditionalServiceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Список пуст (нет заказанных услуг)",
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
    public ResponseEntity<List<OrderAdditionalServiceResponse>> getMyOrderAdditionalServices(@RequestHeader(ACCOUNT_ID) UUID accountId){
        log.info("Запрос на получение списка заказанных услуг");
        return ResponseEntity.ok(service.getMyOrders(accountId));

    }
}

