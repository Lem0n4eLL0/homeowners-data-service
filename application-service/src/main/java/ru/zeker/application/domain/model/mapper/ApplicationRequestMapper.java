package ru.zeker.application.domain.model.mapper;

import org.mapstruct.Mapper;
import ru.zeker.application.domain.model.dto.request.ApplicationRequest;
import ru.zeker.application.domain.model.entity.Application;

@Mapper
public interface ApplicationRequestMapper {
    Application toEntity(ApplicationRequest applicationDto);
    ApplicationRequest toModel(Application entity);
}
