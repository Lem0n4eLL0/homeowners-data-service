package ru.zeker.authentication.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class AccountAlreadyExistsException extends BaseException {
    public AccountAlreadyExistsException(String message, ErrorCode errorCode) {
        super(message, HttpStatus.CONFLICT, errorCode);
    }
}
