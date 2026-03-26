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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.homeowners.domain.dto.request.MeterIndicationsRequest;
import ru.zeker.homeowners.domain.dto.response.MeterIndicationsResponse;
import ru.zeker.homeowners.service.MetersIndicationService;

@RestController
@RequestMapping("/meters/indications")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Meters Indications API", description = "Управление показаниями приборов учёта: передача текущих значений и просмотр истории")
public class MetersIndicationsController {

  private final MetersIndicationService service;

  @PostMapping
  @Operation(
      summary = "Передача текущих показаний счетчика",
      description = """
                    Эндпоинт для отправки новых показаний прибора учёта:
                    - Привязка показаний к конкретному счетчику по `meterId`
                    - Валидация: показания не могут быть меньше предыдущих (для накопительных счетчиков)
                    - Проверка периода: нельзя передать показания за будущий период
                    - Автоматический расчет расхода на основе разницы с предыдущей записью
                                    
                    **Правила валидации:**
                    - `value` — обязательное, неотрицательное число (до 2 знаков после запятой)
                    - `indicationDate` — обязательная, не может быть в будущем
                    - `comment` — опциональный текст (макс. 500 символов)
                    - `photoUrl` — опциональная ссылка на фото счетчика
                                    
                    **Бизнес-логика:**
                    1. Система находит последнюю запись показаний для данного счетчика
                    2. Если новые показания меньше предыдущих — возвращается ошибка (возможна замена счетчика)
                    3. Создается новая запись с расчетным расходом (если включена авто-калькуляция)
                    """,
      tags = {"Meters Indications API"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Показания успешно приняты и сохранены",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MeterIndicationsResponse.class),
              examples = @ExampleObject(
                  name = "success",
                  value = """
                                            {
                                              "id": "f1a2b3c4-d5e6-7890-abcd-ef1234567890",
                                              "meterId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                              "meterType": "HOT_WATER",
                                              "meterTypeName": "Горячая вода",
                                              "serialNumber": "WTR-2026-001239",
                                              "value": 1250.50,
                                              "previousValue": 1200.00,
                                              "consumption": 50.50,
                                              "indicationDate": "2026-03-26",
                                              "submittedAt": "2026-03-26T21:30:00Z",
                                              "status": "ACCEPTED",
                                              "comment": "Плановая передача показаний",
                                              "photoUrl": "https://storage.example.com/meters/photo123.jpg"
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
                      name = "validation_error",
                      summary = "Некорректные данные в запросе",
                      value = """
                                                    {
                                                      "timestamp": "2026-03-26T21:30:00Z",
                                                      "status": 400,
                                                      "errorCode": "VALIDATION_FAILED",
                                                      "message": "value: значение не может быть отрицательным"
                                                    }
                                                    """
                  ),
                  @ExampleObject(
                      name = "value_less_than_previous",
                      summary = "Показания меньше предыдущих",
                      value = """
                                                    {
                                                      "timestamp": "2026-03-26T21:30:00Z",
                                                      "status": 400,
                                                      "errorCode": "INDICATION_VALUE_INVALID",
                                                      "message": "Новые показания (100.00) не могут быть меньше предыдущих (1200.00). Возможна замена счетчика."
                                                    }
                                                    """
                  ),
                  @ExampleObject(
                      name = "future_date",
                      summary = "Дата показаний в будущем",
                      value = """
                                                    {
                                                      "timestamp": "2026-03-26T21:30:00Z",
                                                      "status": 400,
                                                      "errorCode": "INDICATION_DATE_INVALID",
                                                      "message": "Дата показаний не может быть в будущем"
                                                    }
                                                    """
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Счетчик или недвижимость не найдены",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "meter_not_found",
                      summary = "Счетчик с указанным ID не найден",
                      value = """
                                                    {
                                                      "timestamp": "2026-03-26T21:30:00Z",
                                                      "status": 404,
                                                      "errorCode": "METER_NOT_FOUND",
                                                      "message": "Счетчик не найден в системе"
                                                    }
                                                    """
                  ),
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
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Доступ запрещён (счетчик не принадлежит пользователю)",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "access_denied",
                  summary = "Пользователь не имеет прав на передачу показаний для этого счетчика",
                  value = """
                                            {
                                              "timestamp": "2026-03-26T21:30:00Z",
                                              "status": 403,
                                              "errorCode": "ACCESS_DENIED",
                                              "message": "У вас нет прав на передачу показаний для данного прибора учёта"
                                            }
                                            """
              )
          )
      ),
      @ApiResponse(
          responseCode = "409",
          description = "Конфликт бизнес-правил",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "indication_already_submitted",
                      summary = "Показания за эту дату уже переданы",
                      value = """
                                                    {
                                                      "timestamp": "2026-03-26T21:30:00Z",
                                                      "status": 409,
                                                      "errorCode": "INDICATION_DUPLICATE",
                                                      "message": "Показания за 2026-03-26 уже были переданы"
                                                    }
                                                    """
                  ),
                  @ExampleObject(
                      name = "meter_replaced",
                      summary = "Счетчик заменён, требуется особый порядок передачи",
                      value = """
                                                    {
                                                      "timestamp": "2026-03-26T21:30:00Z",
                                                      "status": 409,
                                                      "errorCode": "METER_REPLACEMENT_REQUIRED",
                                                      "message": "Счетчик был заменён. Для передачи первых показаний нового прибора обратитесь в поддержку"
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
                  name = "persistence_error",
                  summary = "Ошибка сохранения данных",
                  value = """
                                            {
                                              "timestamp": "2026-03-26T21:30:00Z",
                                              "status": 500,
                                              "errorCode": "PERSISTENCE_ERROR",
                                              "message": "Ошибка сохранения данных: попробуйте повторить запрос"
                                            }
                                            """
              )
          )
      )
  })
  public ResponseEntity<MeterIndicationsResponse> addMeterIndications(
      @Parameter(
          description = "Данные для передачи показаний счетчика",
          required = true,
          content = @Content(schema = @Schema(implementation = MeterIndicationsRequest.class))
      )
      @Valid @RequestBody MeterIndicationsRequest request
  ) {
    return ResponseEntity.ok(service.addMeterIndications(request));
  }

  @GetMapping("/property/{propertyId}")
  @Operation(
      summary = "Получение истории показаний для объекта недвижимости",
      description = """
                    Возвращает историю всех переданных показаний для счетчиков, привязанных к указанной недвижимости.
                                    
                    **Фильтрация и сортировка:**
                    - Возвращаются показания всех типов счетчиков (вода, электричество, газ и т.д.)
                    - Результаты отсортированы по дате показания (`indicationDate`) по убыванию (сначала новые)
                    - Включены данные о расходе (разница с предыдущим показанием)
                                    
                    **Пагинация:**
                    - По умолчанию возвращается последние 50 записей
                    - Для получения полной истории используйте параметры `page` и `size` (в будущей версии)
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
                                                "meterId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                                "meterType": "HOT_WATER",
                                                "meterTypeName": "Горячая вода",
                                                "serialNumber": "WTR-2026-001239",
                                                "value": 1250.50,
                                                "previousValue": 1200.00,
                                                "consumption": 50.50,
                                                "indicationDate": "2026-03-26",
                                                "submittedAt": "2026-03-26T21:30:00Z",
                                                "status": "ACCEPTED",
                                                "comment": "Плановая передача показаний"
                                              },
                                              {
                                                "id": "e2b3c4d5-f6a7-8901-bcde-f12345678901",
                                                "meterId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                                "meterType": "HOT_WATER",
                                                "meterTypeName": "Горячая вода",
                                                "serialNumber": "WTR-2026-001239",
                                                "value": 1200.00,
                                                "previousValue": 1150.25,
                                                "consumption": 49.75,
                                                "indicationDate": "2026-02-26",
                                                "submittedAt": "2026-02-26T18:15:00Z",
                                                "status": "ACCEPTED",
                                                "comment": null
                                              },
                                              {
                                                "id": "d3c4d5e6-a7b8-9012-cdef-123456789012",
                                                "meterId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
                                                "meterType": "ELECTRICITY",
                                                "meterTypeName": "Электричество",
                                                "serialNumber": "ELC-2025-005678",
                                                "value": 8542.00,
                                                "previousValue": 8320.50,
                                                "consumption": 221.50,
                                                "indicationDate": "2026-03-25",
                                                "submittedAt": "2026-03-25T20:00:00Z",
                                                "status": "ACCEPTED",
                                                "comment": "Показания переданы через мобильное приложение"
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
          description = "Недвижимость не найдена или нет показаний",
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
                  @ExampleObject(
                      name = "no_indications",
                      summary = "Показания не найдены (пустой список — это не ошибка)",
                      value = """
                                                    []
                                                    """
                  )
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
      @PathVariable("propertyId") @NotNull(message = "Property ID is required") UUID propertyId
  ) {
    return ResponseEntity.ok(service.getHistoryIndications(propertyId));
  }
}