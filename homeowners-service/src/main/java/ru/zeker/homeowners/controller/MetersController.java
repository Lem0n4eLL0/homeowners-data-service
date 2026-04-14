package ru.zeker.homeowners.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.homeowners.domain.dto.request.MeterRequest;
import ru.zeker.homeowners.domain.dto.response.MeterIndicationsResponse;
import ru.zeker.homeowners.domain.dto.response.MetersResponse;
import ru.zeker.homeowners.service.MetersIndicationService;
import ru.zeker.homeowners.service.MetersService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meters")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Meters API", description = "Управление счетчиками")
public class MetersController {

  private final MetersService service;
  private final MetersIndicationService metersIndicationService;

  @PostMapping
  @Operation(
      summary = "Регистрация нового прибора учёта (счетчика)",
      description = """
          Эндпоинт для добавления нового счетчика в систему:
          - Привязка счетчика к лицевому счёту через `propertyId` и тип ресурса
          - Валидация уникальности серийного номера
          - Проверка доступности услуги для данного объекта
          
          **Логика привязки:**
          1. По `propertyId` и типу счетчика (`type`) система находит соответствующий лицевой счёт
          2. Проверяется, что услуга обслуживается нашей компанией
          3. Создаётся запись счетчика с указанным серийным номером
          
          **Типы счетчиков (`MeterType`):**
          - `COLD_WATER` — Холодная вода
          - `HOT_WATER` — Горячая вода
          - `ELECTRICITY` — Электричество
          - `GAS` — Газ
          - `HEATING` — Отопление
          """,
      tags = {"Meters API"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Счетчик успешно зарегистрирован",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MetersResponse.class),
              examples = @ExampleObject(
                  name = "success",
                  value = """
                      {
                        "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                        "serialNumber": "WTR-2026-001239",
                        "type": "HOT_WATER",
                        "typeName": "Горячая вода",
                        "propertyId": "c250bf3d-b023-4016-a8f7-2fc82532bef8",
                        "personalAccountNumber": "1234567890",
                        "installedAt": "2026-03-26T21:00:00Z",
                        "status": "ACTIVE"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Ошибка валидации входных данных",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "missing_fields",
                      summary = "Не передано обязательное поле",
                      value = """
                          {
                            "timestamp": "2026-03-26T21:00:00Z",
                            "status": 400,
                            "errorCode": "VALIDATION_FAILED",
                            "message": "serial number is required"
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "invalid_serial_format",
                      summary = "Некорректный формат серийного номера",
                      value = """
                          {
                            "timestamp": "2026-03-26T21:00:00Z",
                            "status": 400,
                            "errorCode": "VALIDATION_FAILED",
                            "message": "Серийный номер содержит недопустимые символы"
                          }
                          """
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Ресурс не найден (лицевой счёт или услуга)",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "service_not_serviced",
                      summary = "Услуга не обслуживается для данного объекта",
                      value = """
                          {
                            "timestamp": "2026-03-26T21:00:00Z",
                            "status": 404,
                            "errorCode": "SERVICE_NOT_SERVICES",
                            "message": "Услуга 'HOT_WATER' не обслуживается для недвижимости c250bf3d-..."
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "property_not_found",
                      summary = "Недвижимость не найдена",
                      value = """
                          {
                            "timestamp": "2026-03-26T21:00:00Z",
                            "status": 404,
                            "errorCode": "PROPERTY_NOT_FOUND",
                            "message": "Недвижимость с указанным ID не найдена"
                          }
                          """
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Конфликт бизнес-правил",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "meter_already_exists",
                      summary = "Счетчик с таким серийным номером уже зарегистрирован",
                      value = """
                          {
                            "timestamp": "2026-03-26T21:00:00Z",
                            "status": 400,
                            "errorCode": "METER_ALREADY_REGISTERED",
                            "message": "четчик с таким серийным номером уже существует!"
                          }
                          """
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Внутренняя ошибка сервера",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "internal_error",
                  value = """
                      {
                        "timestamp": "2026-03-26T21:00:00Z",
                        "status": 500,
                        "errorCode": "INTERNAL_SERVER_ERROR",
                        "message": "An internal server error occurred. Please try again later"
                      }
                      """
              )
          )
      )
  })
  public ResponseEntity<MetersResponse> addMeter(
      @Parameter(
          description = "Данные для регистрации нового счетчика",
          required = true,
          content = @Content(schema = @Schema(implementation = MeterRequest.class))
      )
      @Valid @RequestBody MeterRequest request
  ) {
    return ResponseEntity.ok(service.addMeter(request));
  }

  @GetMapping("/property/{propertyId}")
  @Operation(
      summary = "Получение списка счетчиков для объекта недвижимости",
      description = """
          Возвращает все зарегистрированные приборы учёта, привязанные к указанной недвижимости.
          
          **Фильтрация:**
          - Данные включают тип ресурса, серийный номер и дату установки
          """,
      tags = {"Meters API"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Список счетчиков успешно получен",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MetersResponse.class),
              examples = @ExampleObject(
                  name = "success_list",
                  value = """
                      [
                        {
                          "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                          "serialNumber": "WTR-2026-001239",
                          "type": "HOT_WATER"
                       
                        },
                        {
                          "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
                          "serialNumber": "ELC-2025-005678",
                          "type": "ELECTRICITY"
                         
                        }
                      ]
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Некорректный формат ID недвижимости",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "invalid_uuid",
                  value = """
                      {
                        "timestamp": "2026-03-26T21:00:00Z",
                        "status": 400,
                        "errorCode": "VALIDATION_FAILED",
                        "message": "Property ID is required"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Недвижимость не найдена",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "property_not_found",
                  value = """
                      {
                        "timestamp": "2026-03-26T21:00:00Z",
                        "status": 404,
                        "errorCode": "PROPERTY_NOT_FOUND",
                        "message": "Недвижимость с указанным ID не найдена"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Внутренняя ошибка сервера",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "internal_error",
                  value = """
                      {
                        "timestamp": "2026-03-26T21:00:00Z",
                        "status": 500,
                        "errorCode": "INTERNAL_SERVER_ERROR",
                        "message": "An internal server error occurred. Please try again later"
                      }
                      """
              )
          )
      )
  })
  public ResponseEntity<List<MetersResponse>> getMeters(
      @Parameter(description = "UUID объекта недвижимости", required = true)
      @PathVariable("propertyId") @NotNull(message = "Property ID is required") UUID propertyId,
      @RequestHeader("Account-Id") UUID accountId
  ) {
    return ResponseEntity.ok(service.getMeters(propertyId,accountId));
  }
  @GetMapping("/history")
  @Operation(
      summary = "Получение истории показаний для объекта недвижимости",
      description = """
          Возвращает историю всех переданных показаний для счетчиков, привязанных к указанной недвижимости.

          """,
      tags = {"Meters Indications API"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "История показаний успешно получена",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MeterIndicationsResponse.class),
              examples = @ExampleObject(
                  name = "success_list",
                  value = """
                      [
                        {
                          "id": "f1a2b3c4-d5e6-7890-abcd-ef1234567890",
                          "Meter": {
                            "meterId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                            "meterType": "HOT_WATER",
                          
                            "serialNumber": "WTR-2026-001239",
                          }
                          "value": 1250.50,
                         
                          "createdAt": "2026-03-26",
                          
                        }
                        
                      ]
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Некорректный формат ID недвижимости",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "invalid_uuid",
                  value = """
                      {
                        "timestamp": "2026-03-26T21:30:00Z",
                        "status": 400,
                        "errorCode": "VALIDATION_FAILED",
                        "message": "Property ID is required"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Недвижимость не найдена",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "property_not_found",
                      summary = "Недвижимость не найдена",
                      value = """
                          {
                            "timestamp": "2026-03-26T21:30:00Z",
                            "status": 404,
                            "errorCode": "PROPERTY_NOT_FOUND",
                            "message": "Недвижимость с указанным ID не найдена"
                          }
                          """
                  ),

              }
          )
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Доступ запрещён (пользователь не владеет недвижимостью)",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "access_denied",
                  summary = "Пользователь не имеет прав на просмотр истории для этой недвижимости",
                  value = """
                      {
                        "timestamp": "2026-03-26T21:30:00Z",
                        "status": 403,
                        "errorCode": "ACCESS_DENIED",
                        "message": "У вас нет прав на просмотр показаний для данной недвижимости"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Внутренняя ошибка сервера",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "internal_error",
                  value = """
                      {
                        "timestamp": "2026-03-26T21:30:00Z",
                        "status": 500,
                        "errorCode": "INTERNAL_SERVER_ERROR",
                        "message": "An internal server error occurred. Please try again later"
                      }
                      """
              )
          )
      )
  })
  public ResponseEntity<List<MeterIndicationsResponse>> getHistoryIndications(
      @Parameter(description = "UUID объекта недвижимости", required = true)
      @RequestHeader("Account-Id") UUID accountId
  ) {
    return ResponseEntity.ok(metersIndicationService.getHistoryIndications(accountId));
  }
}