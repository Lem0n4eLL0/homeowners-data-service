package ru.zeker.homeowners.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zeker.homeowners.domain.model.entity.MeterHistoryValue;

public interface MeterHistoryRepository extends JpaRepository<MeterHistoryValue, UUID> {
  List<MeterHistoryValue> findByMeterId(UUID meterId);

}
