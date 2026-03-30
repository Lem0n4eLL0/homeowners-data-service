package ru.zeker.homeowners.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.zeker.homeowners.domain.model.entity.Accrual;

import java.util.UUID;

@Repository
public interface AccrualRepository extends JpaRepository<Accrual, UUID> {

    @EntityGraph(attributePaths = {"personalAccount.personalAccountServices.service", "personalAccount.property"})
    @Query("""
                SELECT DISTINCT a FROM Accrual a
                JOIN a.personalAccount pa
                JOIN pa.property p
                JOIN p.propertyMemberships pm
                JOIN pm.personalData pd
                WHERE pd.accountId = :accountId
            """)
    Page<Accrual> findAllByAccountId(@Param("accountId") UUID accountId, Pageable pageable);
}