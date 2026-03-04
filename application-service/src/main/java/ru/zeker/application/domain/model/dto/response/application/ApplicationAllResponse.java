package ru.zeker.application.domain.model.dto.response.application;

import lombok.Builder;
import ru.zeker.application.domain.model.dto.external.ContactsDto;
import ru.zeker.application.domain.model.dto.external.PersonalDataDto;
import ru.zeker.application.domain.model.dto.external.PropertyDto;
import ru.zeker.application.domain.model.entity.Application;
import ru.zeker.application.domain.model.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
public record ApplicationAllResponse(
        UUID applicationId,
        String title,
        String comment,
        Status status,
        LocalDateTime createdAt,
        PersonalDataDto personalDataDto,
        PropertyDto propertyDto,
        ContactsDto contactsDto


) {
    public static ApplicationAllResponse toApplicationAllResponse(
            Application application,
            PersonalDataDto personalData,
            PropertyDto property,
            ContactsDto contacts) {

        return new ApplicationAllResponse(
                application.getId(),
                application.getTitle(),
                application.getComment(),
                application.getStatus(),
                application.getCreatedAt(),
                personalData,
                property,
                contacts
        );
    }
}
