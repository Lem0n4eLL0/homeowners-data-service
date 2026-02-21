package ru.zeker.authentication.controller;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zeker.common.controller.GlobalExceptionHandler;
import ru.zeker.common.exception.ErrorCode;

import java.util.Map;

@RestControllerAdvice
public class AuthenticationExceptionHandler extends GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Incorrect parameter value", request.getRequestURI(), request.getRequestId(), ErrorCode.INVALID_INPUT);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handleSignatureException(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token", request.getRequestURI(), request.getRequestId(), ErrorCode.AUTHORIZATION_ERROR);
    }

}
