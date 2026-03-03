package ru.zeker.application.domain.model.mapper;

import org.mapstruct.Mapper;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceInfoResponse;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceInfoResponse;

import ru.zeker.application.domain.model.entity.AdditionalServiceEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdditionalServiceInfoMapper {
    AdditionalServiceEntity toEntity(AdditionalServiceInfoResponse AdditionalServiceInfoResponse);
    AdditionalServiceInfoResponse toModel(AdditionalServiceEntity entity);

}
