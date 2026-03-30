package ru.zeker.homeowners.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.MeterHistoryValue;
import ru.zeker.homeowners.domain.model.entity.Property;
@Schema(description = "Тело ответа истории показаний")
public record MeterIndicationsResponse(

    @Schema(description = "id объекта истории показаний", example = "d019f65b-5c55-4070-a4d3-9e946513e2d7")
    UUID id,
    @Schema(description = "id объекта недвижимости", example = "d019f65b-5c55-4070-a4d3-9e946513e2d7")
    UUID propertyId,
    @Schema(description = "Дата создания", example = "2026-03-30T14:02:27.969")
    LocalDateTime createdAt,
    @Schema(description = "Счетчик", example = """
        {
          "id": "1be0f7ac-705b-46d4-89d4-927b778ff7bf",
          "serialNumber": "WTR-2026-001239",
          "type": "HOT_WATER"
        }
        """
    )
    MetersResponse meter,
    @Schema(description = "Значение счетчика", example = "268.0")
    BigDecimal value

) {
  public static MeterIndicationsResponse of(MeterHistoryValue meterHistoryValue, Property property, MetersResponse meter){
   return new MeterIndicationsResponse(
       meterHistoryValue.getId(), property.getId(), meterHistoryValue.getCreatedAt(), meter,meterHistoryValue.getValue()
   );
  }


}
