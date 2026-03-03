package ru.zeker.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zeker.application.domain.model.entity.AdditionalServiceEntity;

import java.util.UUID;

@Repository
public interface AdditionalServiceRepository extends JpaRepository<AdditionalServiceEntity, UUID> {
}
