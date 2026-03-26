package ru.zeker.homeowners.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;

import java.util.Optional;
import java.util.UUID;
import ru.zeker.homeowners.domain.model.enums.MeterType;
import ru.zeker.homeowners.domain.model.enums.ServiceCode;

@Repository
public interface PersonalAccountRepository extends JpaRepository<PersonalAccount, UUID> {

    @Query("SELECT pa FROM PersonalAccount pa " +
            "JOIN FETCH pa.company c " +
            "JOIN FETCH pa.property p " +
            "WHERE pa.personalNumber = :number")
    Optional<PersonalAccount> findByPersonalNumberWithDetails(@Param("number") String number);
    List<PersonalAccount> findAllByPropertyId(UUID propertyId);
    Optional<PersonalAccount> findByPersonalNumberAndCompanyId(String personalNumber, UUID companyId);

    @Query("SELECT pa FROM PersonalAccount pa " +
        "JOIN pa.personalAccountServices pas " +
        "JOIN pas.service s " +
        "WHERE pa.property.id = :propertyId AND s.code = :serviceCode")
    Optional<PersonalAccount> findByPropertyIdAndServiceCode(
        @Param("propertyId") UUID propertyId,
        @Param("serviceCode") ServiceCode serviceCode
    );

}