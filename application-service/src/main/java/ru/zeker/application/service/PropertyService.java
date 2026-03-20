package ru.zeker.application.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import ru.zeker.application.domain.model.dto.response.application.PropertyDto;
@Service
public class PropertyService {
  protected static PropertyDto getPropertyById(List<PropertyDto> properties,UUID propertyId){
    PropertyDto property = properties.stream()
        .filter(p -> p.propertyId().equals(propertyId))
        .findFirst()
        .orElse(null);
    return property;

  }

}
