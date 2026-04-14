package ru.zeker.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Tag(
        name = "Услуги",
        description = "API для работы с услугами: просмотр списка и получение деталей"
)
/**
 * Контроллер для управления дополнительными услугами.
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Просмотра каталога всех доступных услуг</li>
 *   <li>Получения детальной информации о конкретной услуге</li>
 * </ul>
 * <p>
 * <b>Особенности:</b>
 * <ul>
 *   <li>Эндпоинты публичные — не требуют аутентификации</li>
 *   <li>Данные кэшируются на стороне клиента для улучшения производительности</li>
 *   <li>Все ответы в формате JSON с единой структурой ошибок</li>
 * </ul>
 *
 * @see AdditionalService
 * @see AdditionalServiceResponse
 * @see AdditionalServiceInfoResponse
 */
public class AdditionalServiceController {
    private final AdditionalService service;

    @Operation(
            summary = "Получить список всех услуг",
            description = """
                Возвращает краткую информацию по всем доступным дополнительным услугам.
                                        
                **Использование:**
                - Отображение каталога услуг в личном кабинете
                - Фильтрация по категории на клиенте
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdditionalServiceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Список пуст (нет доступных услуг)",
                    content = @Content
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
    @GetMapping
    public ResponseEntity<List<AdditionalServiceResponse>> getAdditionalServices(){
        log.info("Запрос на получение списка услуг");
        return ResponseEntity.ok(service.getAllAdditionalServices());
    }
    @Operation(
            summary = "Получить подробную информацию об услуге",
            description = """
                Возвращает полную информацию об услуге: описание, цена, условия оказания.
                                        
                **Использование:**
                - Детальная страница услуги
                - Расчёт стоимости перед заказом
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Услуга успешно получена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdditionalServiceInfoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный формат ID услуги",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "timestamp": "2026-03-10T12:00:00",
                                  "status": 400,
                                  "errorCode": "INVALID_UUID",
                                  "message": "ID услуги должен быть валидным UUID",
                                  "requestId": "abc-123"
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Услуга с указанным ID не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "timestamp": "2026-03-10T12:00:00",
                                  "status": 404,
                                  "errorCode": "SERVICE_NOT_FOUND",
                                  "message": "Услуга с указанным ID не найдена",
                                  "requestId": "abc-123"
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к услуге запрещён",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "timestamp": "2026-03-10T12:00:00",
                                  "status": 403,
                                  "errorCode": "ACCESS_DENIED",
                                  "message": "У вас нет прав на просмотр этой услуги",
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
//
    @GetMapping("/{id}")
    public ResponseEntity<AdditionalServiceInfoResponse> getAdditionalServices(@PathVariable("id") UUID additionalServiceId){
        log.info("Получение подробной информации о услуге");
        return ResponseEntity.ok(service.getAdditionalService(additionalServiceId));
    }

}
