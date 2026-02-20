package ru.zeker.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zeker.authentication.domain.model.entity.Account;
import ru.zeker.authentication.exception.AccountNotFoundException;
import ru.zeker.authentication.repository.AccountRepository;
import ru.zeker.common.exception.ErrorCode;

import java.util.UUID;

import static ru.zeker.common.util.MaskPhoneUtils.maskPhone;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;

    public Account findByPhone(String phone) {
        return repository.findByPhone(phone)
                .orElseThrow(() -> new AccountNotFoundException("Account with phone " + maskPhone(phone) + " not found", ErrorCode.USER_NOT_FOUND));
    }

    public Account findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " not found", ErrorCode.USER_NOT_FOUND));
    }

    public Boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public Boolean existsByPhone(String phone){
        return repository.existsByPhone(phone);
    }

    @Transactional
    public Account findOrCreateByPhone(String phone) {
        return repository.findByPhone(phone).orElseGet(() ->
                create(phone)
        );
    }

    @Transactional
    public Account create(String phone){
        return repository.save(Account.builder()
                .phone(phone)
                .build());
    }

    @Transactional
    public void update(Account updatedUser) {
        repository.save(updatedUser);
        log.info("Updated Account with ID: {}", updatedUser.getId());
    }

    @Transactional
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new AccountNotFoundException("Account with ID " + id + " not found", ErrorCode.USER_NOT_FOUND);
        }
        repository.deleteById(id);
        log.info("Deleted Account with ID: {}", id);
    }
}
