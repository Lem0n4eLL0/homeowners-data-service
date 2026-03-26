package ru.zeker.homeowners.mapper;

import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zeker.homeowners.domain.dto.request.MeterRequest;
import ru.zeker.homeowners.domain.dto.request.UserProfileVerifyRequest;
import ru.zeker.homeowners.domain.dto.response.MetersResponse;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.domain.model.entity.PersonalData;

@Mapper(componentModel = "spring")
public interface MeterMapper {

  default Meter toEntity(MeterRequest request, PersonalAccount account) {
    if (request == null) return null;

    Meter meter = new Meter();
    meter.setSerialNumber(request.serialNumber());
    meter.setType(request.type());
    meter.setPersonalAccount(account);
    return meter;
  }

  MetersResponse toModel(Meter entity);

  List<MetersResponse> toModel(List<Meter> entities);

//  List<Meter> toEntity(List<MeterRequest> requests);

}
