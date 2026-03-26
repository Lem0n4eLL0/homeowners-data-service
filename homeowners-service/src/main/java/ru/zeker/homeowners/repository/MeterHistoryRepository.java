package ru.zeker.homeowners.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zeker.homeowners.domain.model.entity.MeterHistoryValue;

public interface MeterHistoryRepository extends JpaRepository<MeterHistoryValue, UUID> {
  MeterHistoryValue findByMeterId(UUID meterId);

}
