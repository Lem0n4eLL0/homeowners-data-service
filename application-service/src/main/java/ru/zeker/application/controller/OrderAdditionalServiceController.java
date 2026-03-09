package ru.zeker.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
            summary = "Заказать услугу",
            description = "Создает запись в таблице order_additional_service"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Услуга заказана",
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
    public ResponseEntity<OrderAdditionalServiceResponse> addAdditionalService(@RequestBody OrderAdditionalServiceRequest orderAdditionalServiceRequest,
                                                                               @RequestHeader UUID accountId){
        return ResponseEntity.ok(service.createOrder(accountId,orderAdditionalServiceRequest));

    }
    @GetMapping("/my")
    @Operation(
            summary = "Получить список заказанных услуг пользователем",
            description = "Возвращает информацию "
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список услуг успешно получен",
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
    public ResponseEntity<List<OrderAdditionalServiceResponse>> getMyOrderAdditionalServices(@RequestHeader UUID accountId){
        return ResponseEntity.ok(service.getMyOrders(accountId));

    }
}

