package ru.zeker.homeowners.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zeker.homeowners.domain.dto.response.UserPropertyResponse;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.domain.model.entity.Property;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "personalAccountNumber", source = "personalAccountNumber")
    UserPropertyResponse toDto(Property property, String personalAccountNumber);

    default String getManagedAccountNumber(Property property) {
        return property.getPersonalAccounts().stream()
                .filter(pa -> pa.getCompany().isManagedByUs())
                .findFirst()
                .map(PersonalAccount::getPersonalNumber)
                .orElse(StringUtils.EMPTY);
    }
}