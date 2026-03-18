package ru.zeker.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zeker.application.domain.model.entity.Application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findAllByAccountId(UUID accountId);

    List<Application> findAllByPropertyId(UUID propertyId);
}
