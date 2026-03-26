package ru.zeker.homeowners.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.exception.HomeownersException;
import ru.zeker.homeowners.repository.PersonalAccountRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalAccountService {

    private final PersonalAccountRepository repository;

    public PersonalAccount getByNumberAndCompanyId(String number, UUID companyId) {
        return repository.findByPersonalNumberAndCompanyId(number, companyId)
                .orElseThrow(HomeownersException::accountNotFound);
    }



}
