// src/main/java/ru/zeker/homeowners/service/UserProfileService.java
package ru.zeker.homeowners.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zeker.common.util.AddressNormalizer;
import ru.zeker.homeowners.domain.dto.request.UserProfileVerifyRequest;
import ru.zeker.homeowners.domain.dto.response.UserProfileResponse;
import ru.zeker.homeowners.domain.dto.response.UserPropertyLink;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.domain.model.entity.PersonalData;
import ru.zeker.homeowners.domain.model.entity.Property;
import ru.zeker.homeowners.domain.model.entity.PropertyMembership;
import ru.zeker.homeowners.exception.ProfileVerificationException;
import ru.zeker.homeowners.mapper.PersonalDataMapper;
import ru.zeker.homeowners.repository.PersonalAccountRepository;
import ru.zeker.homeowners.repository.PersonalDataRepository;
import ru.zeker.homeowners.repository.PropertyMembershipRepository;

import java.util.List;
import java.util.Objects;
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

    /**
     * Универсальный метод с гибкой логикой:
     * <ul>
     *   <li>Если переданы firstName + lastName → обновляем/создаем профиль</li>
     *   <li>Если переданы personalAccountNumber + city + street + houseNumber → верифицируем объект</li>
     *   <li>Можно отправить обе группы сразу → выполнится всё</li>
     *   <li>Если ничего не передано → валидация DTO отклонит запрос</li>
     * </ul>
     */
    @Transactional
    public UserProfileResponse verifyAndOnboard(UUID accountId,
                                                UserProfileVerifyRequest request) {

        PersonalData personalData = null;

        // === Работа с профилем ===
        if (request.hasCompleteProfileData()) {

            personalData = personalDataRepository.findByAccountId(accountId)
                    .orElseGet(() -> createNewProfile(accountId, request));

            personalDataMapper.updateFromRequest(request, personalData);
        }

        // === Работа с объектом ===
        if (request.hasCompletePropertyData()) {

            if (Objects.isNull(personalData)) {
                personalData = personalDataRepository.findByAccountId(accountId)
                        .orElseThrow(ProfileVerificationException::profileNotFound);
            }

            performVerification(personalData, request);
        }

        if (Objects.isNull(personalData)) {
            throw ProfileVerificationException.profileNotFound();
        }

        try {
            personalData = personalDataRepository.save(personalData);
        } catch (OptimisticLockingFailureException e) {
            throw ProfileVerificationException.persistenceError("Конфликт версий данных, попробуйте снова");
        } catch (DataIntegrityViolationException e) {
            throw ProfileVerificationException.persistenceError("Нарушение целостности данных");
        }

        return buildProfileResponse(personalData);
    }

    private PersonalData createNewProfile(UUID accountId, UserProfileVerifyRequest request) {
        log.info("Creating new profile for accountId: {}", accountId);
        return personalDataMapper.toEntity(request, accountId);
    }

    private void performVerification(PersonalData personalData, UserProfileVerifyRequest request) {
        PersonalAccount account = personalAccountRepository
                .findByPersonalNumberWithDetails(request.personalAccountNumber())
                .orElseThrow(ProfileVerificationException::accountNotFound);

        if (!account.getCompany().isManagedByUs()) {
            log.warn("Account {} belongs to third-party provider: {}",
                    request.personalAccountNumber(), account.getCompany().getName());
            throw ProfileVerificationException.thirdPartyProvider();
        }

        Property accountProperty = account.getProperty();

        validateAddress(accountProperty, request);

        if (membershipRepository.existsByPersonalDataAndProperty(personalData, accountProperty)) {
            log.info("Property already linked: accountId={}, propertyId={}",
                    personalData.getAccountId(), accountProperty.getId());
            throw ProfileVerificationException.propertyAlreadyLinked();
        }

        personalData.getPropertyMemberships().add(PropertyMembership.builder()
                .personalData(personalData)
                .property(accountProperty)
                .build());
    }

    private void validateAddress(Property property, UserProfileVerifyRequest request) {

        validateField("city",
                "Город",
                property.getCity(),
                request.city(),
                AddressNormalizer::matches);

        validateField("street",
                "Улица",
                property.getStreet(),
                request.street(),
                AddressNormalizer::matches);

        validateField("houseNumber",
                "Дом",
                property.getHouseNumber(),
                request.houseNumber(),
                AddressNormalizer::matches);

        validateOptionalField("corpus",
                "Корпус",
                property.getCorpus(),
                request.corpus());

        validateOptionalField("flatNumber",
                "Квартира",
                property.getFlatNumber(),
                request.flatNumber());
    }

    private void validateField(
            String logField,
            String displayName,
            String expected,
            String actual,
            BiPredicate<String, String> matcher
    ) {
        if (!matcher.test(expected, actual)) {
            logAddressMismatch(logField, expected, actual);
            throw ProfileVerificationException.addressMismatch(displayName, expected, actual);
        }
    }

    private void validateOptionalField(
            String logField,
            String displayName,
            String expected,
            String actual
    ) {
        if (!nullSafeAddressMatch(expected, actual)) {
            logAddressMismatch(logField, expected, actual);
            throw ProfileVerificationException.addressMismatch(
                    displayName,
                    StringUtils.defaultString(expected, "не указан"),
                    StringUtils.defaultString(actual, "не указан")
            );
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
                .orElseThrow(ProfileVerificationException::profileNotFound);
        return buildProfileResponse(data);
    }

    private UserProfileResponse buildProfileResponse(PersonalData data) {
        List<UserPropertyLink> properties = data.getPropertyMemberships().stream()
                .map(membership -> {
                    Property prop = membership.getProperty();

                    String accountNumber = prop.getPersonalAccounts().stream()
                            .filter(pa -> pa.getCompany().isManagedByUs())
                            .findFirst()
                            .map(PersonalAccount::getPersonalNumber)
                            .orElse(StringUtils.EMPTY);

                    return UserPropertyLink.fromProperty(prop, accountNumber);
                })
                .toList();

        return new UserProfileResponse(
                data.getId(),
                data.getFirstName(),
                data.getLastName(),
                data.getSurname(),
                properties
        );
    }
}
