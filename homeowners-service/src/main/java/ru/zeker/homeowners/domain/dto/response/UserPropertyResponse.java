package ru.zeker.homeowners.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.zeker.homeowners.domain.model.entity.Property;

import java.util.UUID;

@Schema(description = "Связанный объект недвижимости")
public record UserPropertyResponse(
        @Schema(description = "ID объекта недвижимости", example = "110e8400-e29b-41d4-a716-446655440000")
        UUID propertyId,

        @Schema(description = "Улица", example = "Ленина")
        String street,

        @Schema(description = "Номер дома", example = "10А")
        String houseNumber,

        @Schema(description = "Номер корпуса", example = "2")
        String corpus,

        @Schema(description = "Номер квартиры", example = "45")
        String flatNumber,

        @Schema(description = "Лицевой счет нашей УК", example = "1234567890")
        String personalAccountNumber
) {
}