package ru.zeker.homeowners.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class HomeownersException extends BaseException {

    public HomeownersException(ErrorCode errorCode, String message, HttpStatus status) {
        super(message, status, errorCode);
    }

    // === Existing factory methods ===

    public static HomeownersException profileNotFound() {
        return new HomeownersException(
                ErrorCode.PROFILE_NOT_FOUND,
                "Профиль не найден. Сначала заполните личные данные.",
                HttpStatus.NOT_FOUND
        );
    }

    public static HomeownersException accountNotFound() {
        return new HomeownersException(
                ErrorCode.ACCOUNT_NOT_FOUND,
                "Лицевой счет не найден в системе",
                HttpStatus.NOT_FOUND
        );
    }
    public static HomeownersException meterNotFound() {
        return new HomeownersException(
            ErrorCode.METER_NOT_FOUNDED,
            "Счетчик не найден в системе",
            HttpStatus.NOT_FOUND
        );
    }

    /**
     * Счет найден, но принадлежит сторонней организации (isManagedByUs = false)
     */
    public static HomeownersException thirdPartyProvider() {
        return new HomeownersException(
                ErrorCode.THIRD_PARTY_PROVIDER,
                "Лицевой счет обслуживается сторонней организацией",
                HttpStatus.FORBIDDEN
        );
    }

    /**
     * Адрес не совпадает с реестром
     */
    public static HomeownersException addressMismatch(String field, String expected, String provided) {
        return new HomeownersException(
                ErrorCode.ADDRESS_MISMATCH,
                String.format("Поле '%s' не совпадает. В реестре: '%s', вы ввели: '%s'", field, expected, provided),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Объект уже привязан к этому пользователю
     */
    public static HomeownersException propertyAlreadyLinked() {
        return new HomeownersException(
                ErrorCode.PROPERTY_ALREADY_LINKED,
                "Эта недвижимость уже привязана к вашему профилю",
                HttpStatus.CONFLICT
        );
    }

    /**
     * Ошибка сохранения (оптимистическая блокировка, уникальность и т.д.)
     */
    public static HomeownersException persistenceError(String details) {
        return new HomeownersException(
                ErrorCode.PERSISTENCE_ERROR,
                "Ошибка сохранения данных: " + details,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Общий случай для бизнес-валидации
     */
    public static HomeownersException invalidInput(String message) {
        return new HomeownersException(
                ErrorCode.INVALID_INPUT,
                message,
                HttpStatus.BAD_REQUEST
        );
    }

    public static HomeownersException profileAlreadyExists() {
        return new HomeownersException(
                ErrorCode.PROFILE_ALREADY_EXISTS,
                "Пользователь уже зарегистрирован",
                HttpStatus.CONFLICT
        );
    }

    public static HomeownersException emailAlreadyConfirmed() {
        return new HomeownersException(
                ErrorCode.EMAIL_ALREADY_CONFIRMED,
                "Email уже подтвержден для этого аккаунта",
                HttpStatus.CONFLICT
        );
    }

    public static HomeownersException emailAlreadyUsed() {
        return new HomeownersException(
                ErrorCode.EMAIL_ALREADY_USED,
                "Email уже используется другим аккаунтом",
                HttpStatus.CONFLICT
        );
    }

    public static HomeownersException emailCooldown() {
        return new HomeownersException(
                ErrorCode.EMAIL_COOLDOWN,
                "Запрос подтверждения email был сделан слишком недавно. Попробуйте позже",
                HttpStatus.TOO_MANY_REQUESTS
        );
    }

    public static HomeownersException invalidEmailFormat() {
        return new HomeownersException(
                ErrorCode.INVALID_EMAIL_FORMAT,
                "Email имеет некорректный формат",
                HttpStatus.BAD_REQUEST
        );
    }

    public static HomeownersException emailVerificationFailed() {
        return new HomeownersException(
                ErrorCode.EMAIL_VERIFICATION_FAILED,
                "Ошибка при отправке письма для подтверждения email",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}