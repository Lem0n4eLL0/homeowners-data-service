package ru.zeker.authentication.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class ConsentRequiredException extends BaseException {
    public ConsentRequiredException() {
        super("Consent to the processing of personal data must be accepted", HttpStatus.BAD_REQUEST, ErrorCode.PERSONAL_DATA_CONSENT_REQUIRED);
    }
}
