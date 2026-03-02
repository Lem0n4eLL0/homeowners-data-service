package ru.zeker.homeowners.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zeker.homeowners.domain.model.entity.PersonalData;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonalDataRepository extends JpaRepository<PersonalData, UUID> {

    @EntityGraph(attributePaths = {
            "propertyMemberships",
            "propertyMemberships.property",
            "propertyMemberships.property.personalAccounts",
            "propertyMemberships.property.personalAccounts.company"
    })
    Optional<PersonalData> findByAccountId(UUID accountId);

    boolean existsByAccountId(UUID accountId);
}