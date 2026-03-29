package ru.zeker.homeowners.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.zeker.homeowners.domain.dto.request.MeterRequest;
import ru.zeker.homeowners.domain.dto.response.MetersResponse;
import ru.zeker.homeowners.domain.dto.response.UserProfileResponse;
import ru.zeker.homeowners.domain.dto.response.UserPropertyResponse;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.domain.model.entity.Property;
import ru.zeker.homeowners.domain.model.enums.ServiceCode;
import ru.zeker.homeowners.exception.HomeownersException;
import ru.zeker.homeowners.exception.SerialNumberAlreadyExist;
import ru.zeker.homeowners.mapper.MeterMapper;
import ru.zeker.homeowners.repository.MetersRepository;
import ru.zeker.homeowners.repository.PersonalAccountRepository;

/**
 * Сервис для управления приборами учёта (счетчиками).
 * <p>
 * Отвечает за бизнес-логику:
 * <ul>
 *   <li>Получение списка счетчиков для объекта недвижимости</li>
 *   <li>Регистрация нового счетчика с привязкой к лицевому счёту</li>
 *   <li>Валидация доступности услуги для данного объекта</li>
 * </ul>
 * </p>
 *
 *
 * @author CatCus12
 * @version 1.0
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class MetersService {

  private final MetersRepository repository;
  private final PersonalAccountRepository personalAccountRepository;
  private final MeterMapper mapper;
  private final UserProfileService userProfileService;

  /**
   * Получение списка всех счетчиков, привязанных к объекту недвижимости.
   *
   * <p><strong>Алгоритм работы:</strong></p>
   * <ol>
   *   <li>Поиск всех лицевых счетов ({@code PersonalAccount}) для указанного {@code propertyId}</li>
   *   <li>Если счета не найдены — выбрасывается {@code HomeownersException.accountNotFound()}</li>
   *   <li>Для каждого лицевого счета загружаются связанные счетчики ({@code Meter})</li>
   *   <li>Сущности маппятся в DTO ({@code MetersResponse}) и aгрегируются в общий список</li>
   * </ol>
   *
   * <p><strong>Бизнес-правила:</strong></p>
   * <ul>
   *   <li>Один объект недвижимости может иметь несколько лицевых счетов (для разных услуг)</li>
   *   <li>Счетчики возвращаются из всех лицевых счетов, привязанных к недвижимости</li>
   *   <li>Порядок следования счетчиков в ответе не гарантируется</li>
   * </ul>
   *
   * @param propertyId UUID объекта недвижимости
   * @return список DTO с информацией о счетчиках
   * @throws HomeownersException с кодом {@code ACCOUNT_NOT_FOUND}, если для {@code propertyId} не найдено ни одного лицевого счета
   * @see PersonalAccountRepository#findAllByPropertyId(UUID)
   * @see MetersRepository#findByPersonalAccountId(UUID)
   */
  public List<MetersResponse> getMeters(UUID propertyId,UUID accountId) {
    List<MetersResponse> response = new ArrayList<>();
    List<PersonalAccount> personalAccounts = personalAccountRepository.findAllByPropertyId(propertyId);
    UserProfileResponse userProfileData = userProfileService.getProfileResponse(accountId);
    boolean hasAccess = userProfileData.properties().stream()
        .anyMatch(property -> property.propertyId().equals(propertyId));

    if (!hasAccess) {
      throw HomeownersException.propertyNotFound();
    }

    if (personalAccounts.isEmpty()) {
      log.info("Не найдено лицевых счетов у недвижимости с propertyId = "+propertyId);
      throw HomeownersException.accountNotFound();
    }

    for (PersonalAccount personalAccount : personalAccounts) {
      List<Meter> meters = repository.findByPersonalAccountId(personalAccount.getId());
      response.addAll(mapper.toModel(meters));
    }
    return response;
  }

  /**
   * Регистрация нового прибора учёта (счетчика).
   *
   * <p><strong>Алгоритм работы:</strong></p>
   * <ol>
   *
   *   <li>Поиск лицевых счетов, которые:
   *     <ul>
   *       <li>Привязаны к указанной недвижимости {@code propertyId}</li>
   *       <li>Имеют связь с услугой, соответствующей типу счетчика ({@code ServiceCode})</li>
   *     </ul>
   *   </li>
   *   <li>Если подходящие счета не найдены — выбрасывается {@code HomeownersException.ServiceNotServicedException()}</li>
   *   <li>Берётся первый найденный лицевой счёт (индекс {@code 0})</li>
   *   <li>Создаётся сущность {@code Meter} и сохраняется в БД</li>
   *   <li>Сохранённая сущность маппится в DTO и возвращается</li>
   * </ol>
   *
   * <p><strong>Бизнес-правила и ограничения:</strong></p>
   *
   *
   * <p><strong>Возможные исключения:</strong></p>
   * <ul>
   *   <li>{@code IllegalArgumentException} — если {@code request.type().name()} не соответствует ни одной константе в {@code ServiceCode}</li>
   *   <li>{@code HomeownersException} с кодом {@code SERVICE_NOT_SERVICES} — если не найден лицевой счёт для указанной услуги</li>
   *   <li>{@code org.springframework.dao.DataIntegrityViolationException} — при нарушении уникальности или внешних ключей в БД</li>
   * </ul>
   *
   * @param request DTO с данными для регистрации: {@code propertyId}, {@code type} (MeterType), {@code serialNumber}
   * @return DTO с информацией о созданном счетчике
   * @throws HomeownersException если услуга не обслуживается для данного объекта
   * @throws IllegalArgumentException если тип счетчика не имеет соответствия в ServiceCode
   * @see ServiceCode#valueOf(String)
   * @see PersonalAccountRepository#findByPropertyIdAndServiceCode(UUID, ServiceCode)
   */
  public MetersResponse addMeter(MeterRequest request) {
    log.info("Добавление счетчика");

    log.info("Поиск лицевых счетов для типа счетчика");
    List<PersonalAccount> personalAccounts = personalAccountRepository
        .findByPropertyIdAndServiceCode(request.propertyId(),request.type());
    Meter meter;

    if (personalAccounts.isEmpty()) {
      throw HomeownersException.ServiceNotServicedException();
    }
    log.info("Сохранение счетчика");
    try{
      meter = repository.save(mapper.toEntity(request, personalAccounts.get(0)));
    }catch (DataIntegrityViolationException e){
      throw new SerialNumberAlreadyExist();
    }

    return mapper.toModel(meter);
  }
}