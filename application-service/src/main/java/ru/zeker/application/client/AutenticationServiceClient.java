package ru.zeker.application.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.zeker.application.config.FeignConfig;
import ru.zeker.application.domain.model.dto.external.ContactsDto;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;
/**
 * Feign-клиент для взаимодействия с authentication-service.
 * Предоставляет методы для получения данных аккаунта и контактов пользователя.
 */
@FeignClient(
        name = "authentication-service",
        url = "${authentication.service.url:http://authentication-service:8080}",
configuration = FeignConfig .class
)
public interface AutenticationServiceClient {
    /**
     * Получение контактных данных текущего пользователя.
     * <p>
     * Вызывает эндпоинт {@code GET /accounts/me} в authentication-service.
     * Требует заголовок {@code Account-Id} для идентификации пользователя.
     *
     *
     * @return {@link ContactsDto} с телефоном и email пользователя
     * @throws feign.FeignException если сервис недоступен или возвращает ошибку
     */

    @Operation(
            summary = "Получить контактные данные пользователя",
            description = """
                    Возвращает телефон и email пользователя по его accountId.
                                        
                    **Требования:**
                    - Заголовок `Account-Id` должен быть передан
                    - Пользователь должен быть аутентифицирован
                    """,
            tags = {"Authentication Service API"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Контактные данные успешно получены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContactsDto.class)
                    ),
                    headers = {
                            @Header(name = "X-Request-ID", description = "ID запроса для трассировки",
                                    schema = @Schema(type = "string"))
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован или отсутствует заголовок аутентификации",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                        {
                          "timestamp": "2026-03-10T12:00:00",
                          "status": 401,
                          "errorCode": "UNAUTHORIZED",
                          "message": "Требуется авторизация"
                        }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Аккаунт не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 404,
                                      "errorCode": "ACCOUNT_NOT_FOUND",
                                      "message": "Аккаунт с указанным ID не найден"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервиса аутентификации",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 500,
                                      "errorCode": "INTERNAL_SERVER_ERROR",
                                      "message": "An internal server error occurred"
                                    }
                                    """)
                    )
            )
    })

    @GetMapping("/accounts/me")
    public ContactsDto getContacts();
}
