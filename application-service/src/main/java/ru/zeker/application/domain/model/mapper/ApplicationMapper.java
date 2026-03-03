package ru.zeker.application.domain.model.mapper;

import org.mapstruct.Mapper;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;
import ru.zeker.application.domain.model.entity.Application;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
     Application toEntity(ApplicationResponse applicationDto);
     ApplicationResponse toModel(Application entity);
     List<Application> toEntityList(List<ApplicationResponse> applicationDtoList);
     List<ApplicationResponse> toModelList(List<Application> applicationList);
}
