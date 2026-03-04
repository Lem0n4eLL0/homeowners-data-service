package ru.zeker.application.exceptions;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class ResourceAlreadyExistsException extends BaseException {
    public ResourceAlreadyExistsException(String message) {
        super(message +"alreade exist", HttpStatus.CONFLICT, ErrorCode.ALREADY_EXIST);
    }


}
