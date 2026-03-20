package ru.zeker.application.exceptions;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class ServiceException extends BaseException {

    public ServiceException(String message, HttpStatus status, ErrorCode errorCode) {
        super(message, status, errorCode);
    }
}
