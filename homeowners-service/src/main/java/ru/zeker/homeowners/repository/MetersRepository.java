package ru.zeker.homeowners.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zeker.homeowners.domain.model.entity.Meter;

public interface MetersRepository extends JpaRepository<Meter, UUID> {
  List<Meter> findByPersonalAccountId(UUID personalAccountId);
}
