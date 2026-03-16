package ru.zeker.authentication.domain.mapper;

import org.mapstruct.Mapper;
import ru.zeker.authentication.domain.dto.request.SmsRequest;
import ru.zeker.common.dto.response.AccountResponse;
import ru.zeker.authentication.domain.model.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toEntity(SmsRequest request);

    AccountResponse toResponse(Account user);
}
