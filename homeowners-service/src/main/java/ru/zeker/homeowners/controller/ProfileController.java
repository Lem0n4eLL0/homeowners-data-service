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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.homeowners.domain.dto.request.UserProfileVerifyRequest;
import ru.zeker.homeowners.domain.dto.response.UserProfileResponse;
import ru.zeker.homeowners.service.UserProfileService;

import java.util.UUID;

import static ru.zeker.common.headers.AppHeaders.ACCOUNT_ID;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile API", description = "Управление профилем владельца недвижимости и верификация объектов")
public class ProfileController {

    private final UserProfileService profileService;

    /**
     * Универсальный эндпоинт для онбординга и верификации.
     * <p>
     * Поддерживает три сценария:
     * <ul>
     *   <li>Первый вход: передача ФИО + данных объекта → создаётся профиль и привязывается объект</li>
     *   <li>Обновление ФИО: передача только firstName/lastName → профиль обновляется</li>
     *   <li>Добавление объекта: передача только данных объекта → объект привязывается к существующему профилю</li>
     * </ul>
     */
    @PostMapping
    @Operation(
            summary = "Верификация профиля и объекта недвижимости",
            description = """
                    Универсальный эндпоинт для:
                    - Создания/обновления профиля пользователя
                    - Верификации лицевого счета
                    - Привязки объекта недвижимости к профилю
                                        
                    **Правила валидации:**
                    - Для работы с профилем: заполнить `firstName` + `lastName`
                    - Для верификации объекта: заполнить `personalAccountNumber` + `city` + `street` + `houseNumber`
                    - Можно отправить обе группы сразу
                    - `surname`, `corpus`, `flatNumber` — всегда опциональные
                                        
                    **Нормализация адреса:**
                    Система автоматически нормализует ввод: "ул." → "улица", регистр не учитывается, лишние пробелы удаляются.
                    """,
            tags = {"Profile API"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль успешно обновлен и/или объект привязан",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                            {
                                              "id": "550e8400-e29b-41d4-a716-446655440000",
                                              "firstName": "Иван",
                                              "lastName": "Иванов",
                                              "surname": "Иванович",
                                              "properties": [
                                                {
                                                  "propertyId": "110e8400-e29b-41d4-a716-446655440000",
                                                  "city": "Москва",
                                                  "street": "Ленина",
                                                  "houseNumber": "10",
                                                  "corpus": null,
                                                  "flatNumber": "55",
                                                  "personalAccountNumber": "1234567890"
                                                }
                                              ]
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
                                            name = "empty_request",
                                            summary = "Не передано ни ФИО, ни данных объекта",
                                            value = """
                                                    {
                                                      "timestamp": "2024-01-15T10:00:00",
                                                      "status": 400,
                                                      "errorCode": "VALIDATION_FAILED",
                                                      "message": "Нужно заполнить либо профиль (firstName + lastName), либо объект (personalAccountNumber + city + street + houseNumber)"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid_account_format",
                                            summary = "Лицевой счет содержит буквы",
                                            value = """
                                                    {
                                                      "timestamp": "2024-01-15T10:00:00",
                                                      "status": 400,
                                                      "errorCode": "VALIDATION_FAILED",
                                                      "message": "Лицевой счет должен содержать только цифры"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ресурс не найден",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "account_not_found",
                                            summary = "Лицевой счет не найден в системе",
                                            value = """
                                                    {
                                                      "timestamp": "2024-01-15T10:00:00",
                                                      "status": 404,
                                                      "errorCode": "ACCOUNT_NOT_FOUND",
                                                      "message": "Лицевой счет не найден в системе"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "profile_not_found",
                                            summary = "Профиль не найден (при попытке привязать объект без создания профиля)",
                                            value = """
                                                    {
                                                      "timestamp": "2024-01-15T10:00:00",
                                                      "status": 404,
                                                      "errorCode": "PROFILE_NOT_FOUND",
                                                      "message": "Профиль не найден. Сначала заполните личные данные."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт бизнес-правил",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "address_mismatch",
                                            summary = "Адрес не совпадает с данными реестра",
                                            value = """
                                                    {
                                                      "timestamp": "2024-01-15T10:00:00",
                                                      "status": 409,
                                                      "errorCode": "ADDRESS_MISMATCH",
                                                      "message": "Улица не совпадает. В реестре: 'Ленина', вы ввели: 'Пушкина'"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "property_already_linked",
                                            summary = "Объект уже привязан к профилю",
                                            value = """
                                                    {
                                                      "timestamp": "2024-01-15T10:00:00",
                                                      "status": 409,
                                                      "errorCode": "PROPERTY_ALREADY_LINKED",
                                                      "message": "Эта недвижимость уже привязана к вашему профилю"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещён",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "third_party_provider",
                                    summary = "Счет принадлежит сторонней организации",
                                    value = """
                                            {
                                              "timestamp": "2024-01-15T10:00:00",
                                              "status": 403,
                                              "errorCode": "THIRD_PARTY_PROVIDER",
                                              "message": "Лицевой счет обслуживается сторонней организацией: ООО 'ГорСвет'"
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
                                    name = "persistence_error",
                                    summary = "Ошибка сохранения данных",
                                    value = """
                                            {
                                              "timestamp": "2024-01-15T10:00:00",
                                              "status": 500,
                                              "errorCode": "PERSISTENCE_ERROR",
                                              "message": "Конфликт версий данных, попробуйте снова"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<UserProfileResponse> verifyAndProfile(
            @Parameter(description = "Unique user identifier", hidden = true)
            @RequestHeader(ACCOUNT_ID) @NotNull UUID accountId,

            @Parameter(
                    description = "Данные для обновления профиля и/или верификации объекта",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserProfileVerifyRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "first_login",
                                            summary = "Первый вход: ФИО + объект",
                                            value = """
                                                    {
                                                      "firstName": "Иван",
                                                      "lastName": "Иванов",
                                                      "surname": "Иванович",
                                                      "personalAccountNumber": "1234567890",
                                                      "city": "Москва",
                                                      "street": "ул. Ленина",
                                                      "houseNumber": "10",
                                                      "corpus": "2",
                                                      "flatNumber": "55"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "add_property_only",
                                            summary = "Только добавить объект (профиль уже есть)",
                                            value = """
                                                    {
                                                      "personalAccountNumber": "9999999999",
                                                      "city": "Москва",
                                                      "street": "пер. Пушкина",
                                                      "houseNumber": "5"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "update_name_only",
                                            summary = "Только обновить ФИО",
                                            value = """
                                                    {
                                                      "firstName": "Иван",
                                                      "lastName": "Петров"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody UserProfileVerifyRequest request
    ) {
        return ResponseEntity.ok(profileService.verifyAndOnboard(accountId, request));
    }

    /**
     * Получение текущего профиля пользователя.
     */
    @GetMapping("/me")
    @Operation(
            summary = "Получение профиля пользователя",
            description = "Возвращает текущие данные профиля и список привязанных объектов недвижимости",
            tags = {"Profile API"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Профиль не найден",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-01-15T10:00:00",
                                              "status": 404,
                                              "errorCode": "PROFILE_NOT_FOUND",
                                              "message": "Профиль не найден. Сначала заполните личные данные."
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<UserProfileResponse> getProfile(
            @Parameter(description = "Unique user identifier", hidden = true)
            @RequestHeader(ACCOUNT_ID) @NotNull UUID accountId
    ) {
        return ResponseEntity.ok(profileService.getProfileResponse(accountId));
    }
}