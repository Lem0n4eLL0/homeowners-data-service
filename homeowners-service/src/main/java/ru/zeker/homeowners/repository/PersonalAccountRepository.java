package ru.zeker.homeowners.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonalAccountRepository extends JpaRepository<PersonalAccount, UUID> {

    @Query("SELECT pa FROM PersonalAccount pa " +
            "JOIN FETCH pa.company c " +
            "JOIN FETCH pa.property p " +
            "WHERE pa.personalNumber = :number")
    Optional<PersonalAccount> findByPersonalNumberWithDetails(@Param("number") String number);
}