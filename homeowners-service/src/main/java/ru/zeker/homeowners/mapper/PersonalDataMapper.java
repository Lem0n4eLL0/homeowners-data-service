package ru.zeker.homeowners.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zeker.homeowners.domain.dto.request.UserProfileVerifyRequest;
import ru.zeker.homeowners.domain.model.entity.PersonalData;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PersonalDataMapper {

    @Mapping(target = "propertyMemberships", ignore = true)
    @Mapping(target = "accountId", source = "accountId")
    PersonalData toEntity(UserProfileVerifyRequest request, UUID accountId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "propertyMemberships", ignore = true)
    void updateFromRequest(UserProfileVerifyRequest request,
                           @MappingTarget PersonalData entity);

}