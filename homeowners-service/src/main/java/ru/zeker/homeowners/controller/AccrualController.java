package ru.zeker.homeowners.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.common.headers.AppHeaders;
import ru.zeker.homeowners.domain.dto.response.AccrualResponse;
import ru.zeker.homeowners.service.AccrualService;

import java.util.UUID;

@RestController
@RequestMapping("/accrual")
@RequiredArgsConstructor
@Validated
@Tag(name = "Accruals", description = "API для работы с начислениями пользователя")
public class AccrualController {

    private final AccrualService accrualService;

    @Operation(
            summary = "Получить начисления пользователя",
            description = "Возвращает страницу начислений для текущего пользователя по accountId. " +
                    "Поддерживается пагинация и сортировка через query-параметры Pageable."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно возвращены начисления"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
    })
    @GetMapping("/me")
    public ResponseEntity<Page<AccrualResponse>> getAccruals(
            @RequestHeader(AppHeaders.ACCOUNT_ID) @NotNull UUID accountId,
            @Parameter(description = "Параметры пагинации и сортировки")
            @PageableDefault(size = 20, sort = "period", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(accrualService.getAccruals(accountId, pageable));
    }
}