package ru.zeker.application.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.zeker.application.config.FeignConfig;
import ru.zeker.application.domain.model.dto.response.application.PersonalDataDto;
import ru.zeker.application.domain.model.dto.response.application.PropertyDto;
import ru.zeker.application.domain.model.dto.response.application.UserProfileDto;
import ru.zeker.common.headers.AppHeaders;

/**
 * Feign-клиент для взаимодействия с homeowners-service.
 * Предоставляет методы для получения профиля пользователя и данных об объектах недвижимости.
 */

@FeignClient(
        name = "homeowners-service",
        url = "${homeowners.service.url:http://homeowners-service:8080}",
//        configuration = FeignConfig.class,
        contextId = "homeownersServiceClient"
)
@Tag(name = "Homeowners Service API", description = "Клиент для вызовов к сервису управления жильцами и недвижимостью")
public interface HomeownersServiceClient {

    /**
     * Получение профиля текущего пользователя.
     * <p>
     * Вызывает эндпоинт {@code GET /profile/me} в homeowners-service.
     * Возвращает личные данные пользователя и список привязанных объектов недвижимости.
     *
     *
     * @return {@link PersonalDataDto} с данными профиля и списком объектов
     * @throws feign.FeignException если сервис недоступен или возвращает ошибку
     */

    @Operation(
            summary = "Получить профиль пользователя",
            description = """
                    Возвращает личные данные (ФИО) и список привязанных объектов недвижимости.
                                        
                    **Требования:**
                    - Access токен в заголовке
                    - Пользователь должен иметь профиль в системе
                    """,
            tags = {"Homeowners Service API"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PersonalDataDto.class)
                    ),
                    headers = {
                            @Header(name = "X-Request-ID", description = "ID запроса для трассировки",
                                    schema = @Schema(type = "string"))
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный запрос (отсутствует или невалидный Account-Id в токене)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 400,
                                      "errorCode": "INVALID_REQUEST",
                                      "message": "Заголовок 'Account-Id' должен содержать валидный UUID"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Профиль пользователя не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2026-03-10T12:00:00",
                                      "status": 404,
                                      "errorCode": "PROFILE_NOT_FOUND",
                                      "message": "Профиль не найден. Сначала заполните личные данные."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервиса",
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

    @GetMapping("/profile/me")
    public PersonalDataDto getPersonalData( @RequestHeader(AppHeaders.ACCOUNT_ID) UUID accountId);

  @GetMapping("/profile/me")
  public UserProfileDto getFullPersonalData( @RequestHeader(AppHeaders.ACCOUNT_ID) UUID accountId);






}
