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
public class AdditionalServiceController {
    private final AdditionalService service;

    @Operation(
            summary = "Получить список всех услуг",
            description = "Возвращает краткую информацию по всем доступным дополнительным услугам " +
                    "(название, базовая цена, категория). Используется для отображения каталога услуг в личном кабинете."
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
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<AdditionalServiceResponse>> getAdditionalServices(){
        log.info("Запрос на получение списка заявок");
        return ResponseEntity.ok(service.getAllAdditionalServices());
    }
    @Operation(
            summary = "Получение подробной информации о услуге",
            description = "Возвращает краткую информацию по всем доступным дополнительным услугам " +
                    "(название, базовая цена, категория). Используется для отображения каталога услуг в личном кабинете."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Услуга получена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdditionalServiceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено информации по услуге",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdditionalServiceInfoResponse> getAdditionalServices(@PathVariable("id") UUID additionalServiceId){
        log.info("Получение подробной информации о услуге");
        return ResponseEntity.ok(service.getAdditionalService(additionalServiceId));
    }

}
