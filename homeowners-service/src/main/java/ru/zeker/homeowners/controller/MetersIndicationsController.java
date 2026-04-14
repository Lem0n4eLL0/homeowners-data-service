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
import org.springframework.web.bind.annotation.RequestHeader;
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
                         Meter:{
                         "meterId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                         "SerialNumber": "WTR-2026-002143"
                         "ServiceCode": "HOT_WATER"
                        }
                        "value": 1250.50,
                      
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


}