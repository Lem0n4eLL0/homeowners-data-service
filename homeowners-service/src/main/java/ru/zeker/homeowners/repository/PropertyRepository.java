package ru.zeker.homeowners.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zeker.homeowners.domain.model.entity.Property;

import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
}