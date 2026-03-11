// src/main/java/ru/zeker/homeowners/exception/ProfileVerificationException.java
package ru.zeker.homeowners.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class ProfileVerificationException extends BaseException {

    public ProfileVerificationException(ErrorCode errorCode, String message, HttpStatus status) {
        super(message, status, errorCode);
    }

    // === Existing factory methods ===

    public static ProfileVerificationException profileNotFound() {
        return new ProfileVerificationException(
                ErrorCode.PROFILE_NOT_FOUND,
                "Профиль не найден. Сначала заполните личные данные.",
                HttpStatus.NOT_FOUND
        );
    }

    public static ProfileVerificationException accountNotFound() {
        return new ProfileVerificationException(
                ErrorCode.ACCOUNT_NOT_FOUND,
                "Лицевой счет не найден в системе",
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Счет найден, но принадлежит сторонней организации (isManagedByUs = false)
     */
    public static ProfileVerificationException thirdPartyProvider() {
        return new ProfileVerificationException(
                ErrorCode.THIRD_PARTY_PROVIDER,
                "Лицевой счет обслуживается сторонней организацией",
                HttpStatus.FORBIDDEN
        );
    }

    /**
     * Адрес не совпадает с реестром
     */
    public static ProfileVerificationException addressMismatch(String field, String expected, String provided) {
        return new ProfileVerificationException(
                ErrorCode.ADDRESS_MISMATCH,
                String.format("Поле '%s' не совпадает. В реестре: '%s', вы ввели: '%s'", field, expected, provided),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Объект уже привязан к этому пользователю
     */
    public static ProfileVerificationException propertyAlreadyLinked() {
        return new ProfileVerificationException(
                ErrorCode.PROPERTY_ALREADY_LINKED,
                "Эта недвижимость уже привязана к вашему профилю",
                HttpStatus.CONFLICT
        );
    }

    /**
     * Ошибка сохранения (оптимистическая блокировка, уникальность и т.д.)
     */
    public static ProfileVerificationException persistenceError(String details) {
        return new ProfileVerificationException(
                ErrorCode.PERSISTENCE_ERROR,
                "Ошибка сохранения данных: " + details,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Общий случай для бизнес-валидации
     */
    public static ProfileVerificationException invalidInput(String message) {
        return new ProfileVerificationException(
                ErrorCode.INVALID_INPUT,
                message,
                HttpStatus.BAD_REQUEST
        );
    }

    public static ProfileVerificationException profileAlreadyExists() {
        return new ProfileVerificationException(
                ErrorCode.PROFILE_ALREADY_EXISTS,
                "Пользователь уже зарегистрирован",
                HttpStatus.CONFLICT
        );
    }

    public static ProfileVerificationException emailAlreadyConfirmed() {
        return new ProfileVerificationException(
                ErrorCode.EMAIL_ALREADY_CONFIRMED,
                "Email уже подтвержден для этого аккаунта",
                HttpStatus.CONFLICT
        );
    }

    public static ProfileVerificationException emailAlreadyUsed() {
        return new ProfileVerificationException(
                ErrorCode.EMAIL_ALREADY_USED,
                "Email уже используется другим аккаунтом",
                HttpStatus.CONFLICT
        );
    }

    public static ProfileVerificationException emailCooldown() {
        return new ProfileVerificationException(
                ErrorCode.EMAIL_COOLDOWN,
                "Запрос подтверждения email был сделан слишком недавно. Попробуйте позже",
                HttpStatus.TOO_MANY_REQUESTS
        );
    }

    public static ProfileVerificationException invalidEmailFormat() {
        return new ProfileVerificationException(
                ErrorCode.INVALID_EMAIL_FORMAT,
                "Email имеет некорректный формат",
                HttpStatus.BAD_REQUEST
        );
    }

    public static ProfileVerificationException emailVerificationFailed() {
        return new ProfileVerificationException(
                ErrorCode.EMAIL_VERIFICATION_FAILED,
                "Ошибка при отправке письма для подтверждения email",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}