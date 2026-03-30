package ru.zeker.homeowners.exception;

import org.springframework.http.HttpStatus;
import ru.zeker.common.exception.BaseException;
import ru.zeker.common.exception.ErrorCode;

public class IndicationsAlreadyTransmiited extends BaseException {

  public IndicationsAlreadyTransmiited(){
    super("Показания за этот период уже переданы", HttpStatus.CONFLICT, ErrorCode.ALREADY_EXIST);
  }
}
