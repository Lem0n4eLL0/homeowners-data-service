package ru.zeker.homeowners.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class SerialNumberAlreadyExist extends BaseException {

  public SerialNumberAlreadyExist(){
    super("Счетчик с таким серийным номером уже существует!", HttpStatus.CONFLICT, ErrorCode.ALREADY_EXIST);
  }

}
