package ru.zeker.homeowners.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zeker.common.controller.GlobalExceptionHandler;
import ru.zeker.common.exception.ErrorCode;
import ru.zeker.homeowners.exception.IndicationsAlreadyTransmiited;
import ru.zeker.homeowners.exception.SerialNumberAlreadyExist;

@RestControllerAdvice
public class HomeOwnersExceptionHandler extends GlobalExceptionHandler {

  @ExceptionHandler(SerialNumberAlreadyExist.class)
  public ResponseEntity<Map<String, Object>> handleSerialNumber(HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Счетчик с таким серийным номером уже существует", request.getRequestURI(), request.getRequestId(), ErrorCode.ALREADY_EXIST);
  }

  @ExceptionHandler(IndicationsAlreadyTransmiited.class)
  public ResponseEntity<Map<String, Object>> handleIndicationAlreadyTransmit(HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Вы уже передавали показания по этому счетчику", request.getRequestURI(), request.getRequestId(), ErrorCode.ALREADY_EXIST);
  }
}
