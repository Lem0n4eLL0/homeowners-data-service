package ru.zeker.homeowners.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zeker.homeowners.domain.model.entity.PersonalData;
import ru.zeker.homeowners.domain.model.entity.Property;
import ru.zeker.homeowners.domain.model.entity.PropertyMembership;

import java.util.UUID;

@Repository
public interface PropertyMembershipRepository extends JpaRepository<PropertyMembership, UUID> {
    boolean existsByPersonalDataAndProperty(PersonalData data, Property property);
}