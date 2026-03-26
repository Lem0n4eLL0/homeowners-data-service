package ru.zeker.homeowners.mapper;

import java.time.LocalDate;
import org.mapstruct.Mapper;
import ru.zeker.homeowners.domain.dto.request.MeterIndicationsRequest;
import ru.zeker.homeowners.domain.dto.request.MeterRequest;
import ru.zeker.homeowners.domain.dto.response.MeterIndicationsResponse;
import ru.zeker.homeowners.domain.dto.response.MetersResponse;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.MeterHistoryValue;

@Mapper(componentModel = "spring")
public interface MeterIndicationsMapper {

  default MeterHistoryValue toEntity(MeterIndicationsRequest request,Meter meter){
    MeterHistoryValue meterHistoryValue = new MeterHistoryValue(meter,request.value(), LocalDate.now());
    return meterHistoryValue;
  };
  MeterIndicationsResponse toModel(MeterHistoryValue entity);

}
