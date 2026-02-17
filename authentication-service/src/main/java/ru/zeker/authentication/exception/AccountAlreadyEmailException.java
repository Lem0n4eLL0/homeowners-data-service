package ru.zeker.authentication.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class AccountAlreadyEmailException extends BaseException {
    public AccountAlreadyEmailException() {
        super("The user is already activated this email", HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_ACTIVATION);
    }
}
