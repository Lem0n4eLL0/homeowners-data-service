// Файл: application-service/src/main/java/ru/zeker/application/controller/ApplicationExceptionHandler.java

package ru.zeker.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zeker.application.exceptions.ResourceNotFoundException;
import ru.zeker.common.controller.GlobalExceptionHandler;
import ru.zeker.common.exception.ErrorCode;

import java.util.Map;

/**
 * Обработчик исключений, специфичных для application-service.
 * Расширяет {@link GlobalExceptionHandler} общими обработчиками.
 */
@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler extends GlobalExceptionHandler {

    /**
     * Услуга не найдена по ID.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleServiceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ResponseEntity<Map<String, Object>> response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                request.getRequestId(),
                ErrorCode.RESOURCE_NOT_FOUND
        );

        if (response.getBody() != null) {
            response.getBody().put("serviceId", ex.getId());
        }
        log.warn("Сервис не найден id={}", ex.getId());
        return response;
    }

//    /**
//     * Доступ к услуге запрещён (бизнес-правила).
//     */
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<Map<String, Object>> handleAccessDenied(
//            AccessDeniedException ex,
//            HttpServletRequest request
//    ) {
//        return buildErrorResponse(
//                HttpStatus.FORBIDDEN,
//                ex.getMessage(),
//                request.getRequestURI(),
//                request.getRequestId(),
//                ErrorCode.ACCESS_DENIED
//        );
//    }

    /**
     * Логирование с контекстом application-service.
     */
    @Override
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("[application-service] Unhandled exception: {} at {}",
                ex.getMessage(), request.getRequestURI(), ex);
        return super.handleGenericException(ex, request);
    }
}