package ru.zeker.authentication.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class BadCredentialsException extends BaseException {
    public BadCredentialsException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ErrorCode.BAD_CREDENTIALS);
    }
}
