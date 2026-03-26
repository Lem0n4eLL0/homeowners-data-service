package ru.zeker.homeowners.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.MeterHistoryValue;
import ru.zeker.homeowners.domain.model.entity.Property;

public record MeterIndicationsResponse(
    UUID id,
    UUID propertyId,
    LocalDateTime createdAt,
    MetersResponse meter,
    BigDecimal value

) {
  public static MeterIndicationsResponse of(MeterHistoryValue meterHistoryValue, Property property, MetersResponse meter){
   return new MeterIndicationsResponse(
       meterHistoryValue.getId(), property.getId(), meterHistoryValue.getCreatedAt(), meter,meterHistoryValue.getValue()
   );
  }


}
