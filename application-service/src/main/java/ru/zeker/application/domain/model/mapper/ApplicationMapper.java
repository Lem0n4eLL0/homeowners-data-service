package ru.zeker.application.domain.model.mapper;

import org.mapstruct.Mapper;
import ru.zeker.application.domain.model.dto.application.ApplicationDto;
import ru.zeker.application.domain.model.entity.Application;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
     Application toEntity(ApplicationDto applicationDto);
     ApplicationDto toModel(Application entity);
     List<Application> toEntityList(List<ApplicationDto> applicationDtoList);
     List<ApplicationDto> toModelList(List<Application> applicationList);
}
