package ru.zeker.application.domain.model.mapper;

import org.mapstruct.Mapper;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceResponse;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;
import ru.zeker.application.domain.model.entity.AdditionalServiceEntity;

import ru.zeker.application.domain.model.entity.Application;

import java.util.List;
@Mapper(componentModel = "spring")
public interface AdditionalServiceMapper {
    AdditionalServiceEntity toEntity(AdditionalServiceResponse additionalServiceResponse);
    AdditionalServiceResponse toModel(AdditionalServiceEntity entity);
    List<AdditionalServiceEntity> toEntityList(List<AdditionalServiceResponse> applicationResponses);
    List<AdditionalServiceResponse> toModelList(List<AdditionalServiceEntity> additionalServices);
}
