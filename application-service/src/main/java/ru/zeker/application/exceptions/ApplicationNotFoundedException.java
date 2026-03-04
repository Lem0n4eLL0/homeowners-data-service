package ru.zeker.application.exceptions;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class ApplicationNotFoundedException extends BaseException {
    public ApplicationNotFoundedException() {
        super("Resource not founded", HttpStatus.NOT_FOUND, ErrorCode.ADDITIONAL_SERVICE_NOT_FOUNDED);
    }
}
