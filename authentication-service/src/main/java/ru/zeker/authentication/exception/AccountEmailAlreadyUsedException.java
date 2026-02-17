package ru.zeker.authentication.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class AccountEmailAlreadyUsedException extends BaseException {

    public AccountEmailAlreadyUsedException() {
        super("The email is already used by another account",
                HttpStatus.CONFLICT,
                ErrorCode.USER_EMAIL_ALREADY_USED);
    }

    public AccountEmailAlreadyUsedException(String email) {
        super(String.format("The email '%s' is already used by another account", email),
                HttpStatus.CONFLICT,
                ErrorCode.USER_EMAIL_ALREADY_USED);
    }
}