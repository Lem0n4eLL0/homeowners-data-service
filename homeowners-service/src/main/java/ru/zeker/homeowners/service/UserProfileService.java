package ru.zeker.homeowners.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zeker.common.dto.request.EmailRequest;
import ru.zeker.common.dto.response.AccountResponse;
import ru.zeker.common.util.AddressNormalizer;
import ru.zeker.homeowners.client.AuthenticationClient;
import ru.zeker.homeowners.domain.dto.request.UserProfileVerifyRequest;
import ru.zeker.homeowners.domain.dto.request.UserPropertyRequest;
import ru.zeker.homeowners.domain.dto.request.UserUpdateProfileRequest;
import ru.zeker.homeowners.domain.dto.response.UserProfileResponse;
import ru.zeker.homeowners.domain.dto.response.UserPropertyResponse;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.domain.model.entity.PersonalData;
import ru.zeker.homeowners.domain.model.entity.Property;
import ru.zeker.homeowners.domain.model.entity.PropertyMembership;
import ru.zeker.homeowners.exception.HomeownersException;
import ru.zeker.homeowners.mapper.PersonalDataMapper;
import ru.zeker.homeowners.mapper.PropertyMapper;
import ru.zeker.homeowners.repository.PersonalAccountRepository;
import ru.zeker.homeowners.repository.PersonalDataRepository;
import ru.zeker.homeowners.repository.PropertyMembershipRepository;

import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final PersonalDataRepository personalDataRepository;
    private final PersonalAccountRepository personalAccountRepository;
    private final PropertyMembershipRepository membershipRepository;
    private final PersonalDataMapper personalDataMapper;
    private final AuthenticationClient authenticationClient;
    private final PropertyMapper propertyMapper;

    @Transactional
    public UserProfileResponse verify(UUID accountId,
                                      UserProfileVerifyRequest request) {

        // === Проверяем, что профиль ещё не существует ===
        if (personalDataRepository.existsByAccountId(accountId)) {
            throw HomeownersException.profileAlreadyExists();
        }

        // === Создаем новый профиль ===
        PersonalData personalData = personalDataMapper.toEntity(request, accountId);
        personalData = personalDataRepository.save(personalData);
        log.info("Создан новый профиль: accountId={}", accountId);

        // === Верификация объекта недвижимости ===
        PersonalAccount account = personalAccountRepository
                .findByPersonalNumberWithDetails(request.personalAccountNumber())
                .orElseThrow(HomeownersException::accountNotFound);

        if (!account.getCompany().isManagedByUs()) {
            log.warn("Лицевой счет {} принадлежит сторонней компании: {}",
                    request.personalAccountNumber(), account.getCompany().getName());
            throw HomeownersException.thirdPartyProvider();
        }

        Property property = account.getProperty();

        validateAddress(property, request);

        if (membershipRepository.existsByPersonalDataAndProperty(personalData, property)) {
            log.warn("Объект уже привязан: accountId={}, propertyId={}",
                    accountId, property.getId());
            throw HomeownersException.propertyAlreadyLinked();
        }

        personalData.getPropertyMemberships().add(PropertyMembership.builder()
                .personalData(personalData)
                .property(property)
                .build());

        try {
            personalData = personalDataRepository.save(personalData);
        } catch (OptimisticLockingFailureException e) {
            throw HomeownersException.persistenceError("Конфликт версий данных, попробуйте снова");
        } catch (DataIntegrityViolationException e) {
            throw HomeownersException.persistenceError("Нарушение целостности данных");
        }

        sendEmailVerification(accountId, request.email());

        return buildProfileResponse(personalData, accountId);
    }

    private void validateAddress(Property property, UserProfileVerifyRequest request) {

        validateField("street",
                property.getStreet(),
                request.street(),
                AddressNormalizer::matches);

        validateField("houseNumber",
                property.getHouseNumber(),
                request.houseNumber(),
                AddressNormalizer::matches);

        validateOptionalField("corpus",
                property.getCorpus(),
                request.corpus());

        validateOptionalField("flatNumber",
                property.getFlatNumber(),
                request.flatNumber());
    }

    private void validateField(
            String logField,
            String expected,
            String actual,
            BiPredicate<String, String> matcher
    ) {
        if (!matcher.test(expected, actual)) {
            logAddressMismatch(logField, expected, actual);
            throw HomeownersException.addressMismatch();
        }
    }

    private void validateOptionalField(
            String logField,
            String expected,
            String actual
    ) {
        if (!nullSafeAddressMatch(expected, actual)) {
            logAddressMismatch(logField, expected, actual);
            throw HomeownersException.addressMismatch();
        }
    }

    private void logAddressMismatch(String field, String dbValue, String inputValue) {
        log.warn("Address mismatch on field '{}': DB='{}' (normalized: '{}'), Input='{}' (normalized: '{}')",
                field,
                dbValue, AddressNormalizer.normalize(dbValue),
                inputValue, AddressNormalizer.normalize(inputValue));
    }

    /**
     * Null-safe сравнение адресных полей с нормализацией.
     * Считает совпадением ситуацию, когда оба значения пустые.
     */
    private boolean nullSafeAddressMatch(String dbVal, String inputVal) {
        boolean dbEmpty = StringUtils.isBlank(dbVal);
        boolean inputEmpty = StringUtils.isBlank(inputVal);

        // Если оба пустые — считаем совпадением
        if (dbEmpty && inputEmpty) {
            return true;
        }
        // Если одно пустое, а другое нет — не совпадает
        if (dbEmpty != inputEmpty) {
            return false;
        }
        // Оба не пустые — сравниваем с нормализацией
        return AddressNormalizer.matches(dbVal, inputVal);
    }

    /**
     * Получение профиля для ответа API.
     * Не использует @Transactional(readOnly), чтобы не усложнять,
     * но можно добавить для оптимизации.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileResponse(UUID accountId) {
        PersonalData data = personalDataRepository.findByAccountId(accountId)
                .orElseThrow(HomeownersException::profileNotFound);
        return buildProfileResponse(data, accountId);
    }

    // === Обновление профиля ===
    @Transactional
    public UserProfileResponse updateProfile(UUID accountId, UserUpdateProfileRequest request) {
        PersonalData personalData = personalDataRepository.findByAccountId(accountId)
                .orElseThrow(HomeownersException::profileNotFound);

        personalDataMapper.updateFromRequest(request, personalData);

        try {
            personalData = personalDataRepository.save(personalData);
        } catch (OptimisticLockingFailureException e) {
            throw HomeownersException.persistenceError("Конфликт версий данных при обновлении профиля");
        } catch (DataIntegrityViolationException e) {
            throw HomeownersException.persistenceError("Нарушение целостности данных при обновлении профиля");
        }

        sendEmailVerification(accountId, request.email());

        return buildProfileResponse(personalData, accountId);
    }

    // === Создание объекта недвижимости ===
    @Transactional
    public UserPropertyResponse createProperty(UUID accountId, UserPropertyRequest request) {
        PersonalData personalData = personalDataRepository.findByAccountId(accountId)
                .orElseThrow(HomeownersException::profileNotFound);

        PersonalAccount account = personalAccountRepository
                .findByPersonalNumberWithDetails(request.personalAccountNumber())
                .orElseThrow(HomeownersException::accountNotFound);

        if (!account.getCompany().isManagedByUs()) {
            throw HomeownersException.thirdPartyProvider();
        }

        Property property = account.getProperty();

        if (membershipRepository.existsByPersonalDataAndProperty(personalData, property)) {
            throw HomeownersException.propertyAlreadyLinked();
        }

        PropertyMembership membership = PropertyMembership.builder()
                .personalData(personalData)
                .property(property)
                .build();

        personalData.getPropertyMemberships().add(membership);

        try {
            personalDataRepository.save(personalData);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw HomeownersException.persistenceError("Ошибка сохранения объекта недвижимости");
        }

        return propertyMapper.toDto(property, request.personalAccountNumber());
    }

    // === Обновление объекта недвижимости ===
    @Transactional
    public UserPropertyResponse updateProperty(UUID accountId, UUID propertyId, UserPropertyRequest request) {
        PersonalData personalData = personalDataRepository.findByAccountId(accountId)
                .orElseThrow(HomeownersException::profileNotFound);

        PropertyMembership membership = membershipRepository.findByPersonalDataAndPropertyId(personalData, propertyId)
                .orElseThrow(() -> HomeownersException.invalidInput("Объект недвижимости не найден для обновления"));

        Property property = membership.getProperty();

        // Обновляем адресные поля, если они пришли
        if (StringUtils.isNotBlank(request.street())) property.setStreet(request.street());
        if (StringUtils.isNotBlank(request.houseNumber())) property.setHouseNumber(request.houseNumber());
        if (request.corpus() != null) property.setCorpus(request.corpus());
        if (StringUtils.isNotBlank(request.flatNumber())) property.setFlatNumber(request.flatNumber());

        try {
            membershipRepository.save(membership);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw HomeownersException.persistenceError("Ошибка обновления объекта недвижимости");
        }

        return propertyMapper.toDto(property, request.personalAccountNumber());
    }

    // === Удаление объекта недвижимости (только связь Membership) ===
    @Transactional
    public UserPropertyResponse deleteProperty(UUID accountId, UUID propertyId) {
        PersonalData personalData = personalDataRepository.findByAccountId(accountId)
                .orElseThrow(HomeownersException::profileNotFound);

        PropertyMembership membership = membershipRepository.findByPersonalDataAndPropertyId(personalData, propertyId)
                .orElseThrow(() -> HomeownersException.invalidInput("Объект недвижимости не найден для удаления"));

        Property property = membership.getProperty();

        personalData.getPropertyMemberships().remove(membership);
        membershipRepository.delete(membership);

        return propertyMapper.toDto(property,
                property.getPersonalAccounts().stream()
                        .filter(pa -> pa.getCompany().isManagedByUs())
                        .findFirst()
                        .map(PersonalAccount::getPersonalNumber)
                        .orElse(StringUtils.EMPTY));
    }

    private UserProfileResponse buildProfileResponse(PersonalData data, UUID accountId) {
        List<UserPropertyResponse> properties = data.getPropertyMemberships().stream()
                .map(membership -> {
                    Property prop = membership.getProperty();

                    String accountNumber = prop.getPersonalAccounts().stream()
                            .filter(pa -> pa.getCompany().isManagedByUs())
                            .findFirst()
                            .map(PersonalAccount::getPersonalNumber)
                            .orElse(StringUtils.EMPTY);

                    return propertyMapper.toDto(prop, accountNumber);
                })
                .toList();

        AccountResponse account = authenticationClient.getAccount(accountId).getBody();

        return new UserProfileResponse(
                data.getId(),
                data.getFirstName(),
                data.getLastName(),
                data.getSurname(),
                account.getEmail(),
                account.getPhone(),
                properties
        );
    }

    private void sendEmailVerification(UUID accountId, String email) {
        if (StringUtils.isBlank(email)) return;

        EmailRequest emailRequest = EmailRequest.builder()
                .email(email)
                .build();

        try {
            authenticationClient.requestEmailVerification(accountId, emailRequest);
        } catch (FeignException e) {
            int status = e.status();
            switch (status) {
                case 409 -> {
                    String body = e.contentUTF8().toLowerCase();
                    if (body.contains("already confirmed")) {
                        log.warn("Email {} уже подтвержден для accountId={}", email, accountId);
                        throw HomeownersException.emailAlreadyConfirmed();
                    } else if (body.contains("already used")) {
                        log.warn("Email {} уже используется другим аккаунтом", email);
                        throw HomeownersException.emailAlreadyUsed();
                    }
                }
                case 429 -> throw HomeownersException.emailCooldown();
                case 400 -> throw HomeownersException.invalidEmailFormat();
                default -> {
                    log.error("Ошибка при запросе подтверждения email для {}: {}", email, e.contentUTF8(), e);
                    throw HomeownersException.emailVerificationFailed();
                }
            }
        }
    }
}
