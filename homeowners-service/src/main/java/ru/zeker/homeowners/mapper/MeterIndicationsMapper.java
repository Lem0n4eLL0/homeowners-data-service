package ru.zeker.homeowners.mapper;

import org.mapstruct.Mapper;
import ru.zeker.homeowners.domain.dto.request.MeterIndicationsRequest;
import ru.zeker.homeowners.domain.dto.request.MeterRequest;
import ru.zeker.homeowners.domain.dto.response.MeterIndicationsResponse;
import ru.zeker.homeowners.domain.dto.response.MetersResponse;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.MeterHistoryValue;

@Mapper(componentModel = "spring")
public interface MeterIndicationsMapper {
  MeterHistoryValue toEntity(MeterIndicationsRequest request);
  MeterIndicationsResponse toModel(MeterHistoryValue entity);

}
