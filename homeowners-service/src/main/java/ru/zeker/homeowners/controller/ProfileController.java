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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.homeowners.domain.dto.request.UserProfileVerifyRequest;
import ru.zeker.homeowners.domain.dto.request.UserPropertyRequest;
import ru.zeker.homeowners.domain.dto.request.UserUpdateProfileRequest;
import ru.zeker.homeowners.domain.dto.response.UserProfileResponse;
import ru.zeker.homeowners.domain.dto.response.UserPropertyResponse;
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

    @PostMapping
    @Operation(
            summary = "Первичная регистрация пользователя и привязка объекта недвижимости",
            description = """
                    Эндпоинт для первичной регистрации нового пользователя:
                    - Создание профиля пользователя (ФИО)
                    - Верификация лицевого счета
                    - Привязка объекта недвижимости к профилю
                                    
                    **Правила валидации:**
                    - `firstName` + `lastName` — обязательны для создания профиля
                    - `personalAccountNumber` + `city` + `street` + `houseNumber` — обязательны для привязки объекта
                    - `surname` и `corpus` — опциональны
                                    
                    **Нормализация адреса:**
                    Система автоматически нормализует ввод: "ул." → "улица", регистр не учитывается, лишние пробелы удаляются.
                    """,
            tags = {"Profile API"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль успешно создан и объект привязан",
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
                                              "email": "ivan@example.com",
                                              "phone": "+79001234567",
                                              "properties": [
                                                {
                                                  "propertyId": "110e8400-e29b-41d4-a716-446655440000",
                                                  "city": "Москва",
                                                  "street": "Ленина",
                                                  "houseNumber": "10",
                                                  "corpus": "2",
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
                                            name = "missing_fields",
                                            summary = "Не передано обязательное поле",
                                            value = """
                                                    {
                                                      "timestamp": "2026-03-11T10:00:00",
                                                      "status": 400,
                                                      "errorCode": "VALIDATION_FAILED",
                                                      "message": "Необходимо заполнить firstName + lastName и personalAccountNumber + city + street + houseNumber"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid_account_format",
                                            summary = "Лицевой счет содержит буквы",
                                            value = """
                                                    {
                                                      "timestamp": "2026-03-11T10:00:00",
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
                                            summary = "Лицевой счет не найден",
                                            value = """
                                                    {
                                                      "timestamp": "2026-03-11T10:00:00",
                                                      "status": 404,
                                                      "errorCode": "ACCOUNT_NOT_FOUND",
                                                      "message": "Лицевой счет не найден в системе"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещён (счет принадлежит сторонней организации)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "third_party_provider",
                                    summary = "Счет обслуживается сторонней организацией",
                                    value = """
                                            {
                                              "timestamp": "2026-03-11T10:00:00",
                                              "status": 403,
                                              "errorCode": "THIRD_PARTY_PROVIDER",
                                              "message": "Лицевой счет обслуживается сторонней организацией: ООО 'ГорСвет'"
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
                                            name = "address_mismatch",
                                            summary = "Адрес не совпадает с данными реестра",
                                            value = """
                                                    {
                                                      "timestamp": "2026-03-11T10:00:00",
                                                      "status": 409,
                                                      "errorCode": "ADDRESS_MISMATCH",
                                                      "message": "Улица не совпадает. В реестре: 'Ленина', вы ввели: 'Пушкина'"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "property_already_linked",
                                            summary = "Объект уже привязан",
                                            value = """
                                                    {
                                                      "timestamp": "2026-03-11T10:00:00",
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
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "persistence_error",
                                    summary = "Ошибка сохранения данных",
                                    value = """
                                            {
                                              "timestamp": "2026-03-11T10:00:00",
                                              "status": 500,
                                              "errorCode": "PERSISTENCE_ERROR",
                                              "message": "Конфликт версий данных, попробуйте снова"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<UserProfileResponse> verifyRegistration(
            @Parameter(description = "Unique user identifier", hidden = true)
            @RequestHeader(ACCOUNT_ID) @NotNull UUID accountId,

            @Parameter(
                    description = "Данные для первичной регистрации пользователя и привязки объекта",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserProfileVerifyRequest.class))
            )
            @Valid @RequestBody UserProfileVerifyRequest request
    ) {
        return ResponseEntity.ok(profileService.verify(accountId, request));
    }

    @PatchMapping
    @Operation(summary = "Обновление информации пользователя (ФИО и email)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль обновлен", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден")
    })
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestHeader(ACCOUNT_ID) UUID accountId,
            @RequestBody @Valid UserUpdateProfileRequest request
    ) {
        UserProfileResponse response = profileService.updateProfile(accountId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/propertie")
    @Operation(summary = "Добавление нового объекта недвижимости")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объект недвижимости создан", content = @Content(schema = @Schema(implementation = UserPropertyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    public ResponseEntity<UserPropertyResponse> createProperty(
            @RequestHeader(ACCOUNT_ID) UUID accountId,
            @RequestBody @Valid UserPropertyRequest request
    ) {
        UserPropertyResponse response = profileService.createProperty(accountId, request);
        return ResponseEntity.ok(response);
    }

    // === Обновление объекта недвижимости ===
    @PatchMapping("/propertie/{id}")
    @Operation(summary = "Обновление объекта недвижимости")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объект недвижимости обновлен", content = @Content(schema = @Schema(implementation = UserPropertyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Объект недвижимости не найден")
    })
    public ResponseEntity<UserPropertyResponse> updateProperty(
            @RequestHeader(ACCOUNT_ID) UUID accountId,
            @PathVariable("id") UUID id,
            @RequestBody @Valid UserPropertyRequest request
    ) {
        UserPropertyResponse response = profileService.updateProperty(accountId, id, request);
        return ResponseEntity.ok(response);
    }

    // === Удаление объекта недвижимости ===
    @DeleteMapping("/propertie/{id}")
    @Operation(summary = "Удаление объекта недвижимости")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объект недвижимости удален", content = @Content(schema = @Schema(implementation = UserPropertyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Объект недвижимости не найден")
    })
    public ResponseEntity<UserPropertyResponse> deleteProperty(
            @RequestHeader(ACCOUNT_ID) UUID accountId,
            @PathVariable("id") UUID id
    ) {
        UserPropertyResponse response = profileService.deleteProperty(accountId, id);
        return ResponseEntity.ok(response);
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