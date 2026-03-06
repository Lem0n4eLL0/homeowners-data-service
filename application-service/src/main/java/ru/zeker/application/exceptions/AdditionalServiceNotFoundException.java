package ru.zeker.application.exceptions;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

import java.util.UUID;

public class AdditionalServiceNotFoundException extends BaseException {
    public AdditionalServiceNotFoundException(){
        super("Resource not founded", HttpStatus.NOT_FOUND,ErrorCode.APPLICATION_SERVICE_NOT_FOUNDED);
    }

}