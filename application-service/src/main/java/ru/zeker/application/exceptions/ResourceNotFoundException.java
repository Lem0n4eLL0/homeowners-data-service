// Файл: application-service/src/main/java/ru/zeker/application/exceptions/AdditionalServiceNotFoundException.java

package ru.zeker.application.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

import java.util.UUID;

/**
 * Исключение: дополнительная услуга не найдена по указанному ID.
 */
@Getter
public class ResourceNotFoundException extends BaseException {

    private final UUID id;

    /**
     * Конструктор с ID услуги.
     * @param id UUID услуги, которая не найдена
     */
    public ResourceNotFoundException(UUID id) {
        super(
                "Ресурс с ID " + id + " не найдена",
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND
        );
        this.id = id;
    }

    /**
     * Конструктор с кастомным сообщением.
     */
    public ResourceNotFoundException(UUID id, String customMessage) {
        super(customMessage, HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND);
        this.id = id;
    }
}