package ru.zeker.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zeker.application.domain.model.entity.OrderAdditional;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderAdditionalServiceRepository extends JpaRepository<OrderAdditional, UUID> {
    public List<OrderAdditional> findAllByAccountId(UUID accountId);
    public List<OrderAdditional> findAllByPropertyId(UUID propertyId);

}
