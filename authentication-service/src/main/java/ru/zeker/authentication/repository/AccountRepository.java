package ru.zeker.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zeker.authentication.domain.model.entity.Account;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByPhone(String phone);
    Boolean existsByPhone(String phone);
    Boolean existsByEmail(String email);
}
