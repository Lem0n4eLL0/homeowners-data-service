package ru.zeker.homeowners.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.zeker.homeowners.domain.dto.request.MeterIndicationsRequest;
import ru.zeker.homeowners.domain.dto.response.MeterIndicationsResponse;
import ru.zeker.homeowners.domain.dto.response.UserProfileResponse;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.MeterHistoryValue;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.domain.model.entity.Property;
import ru.zeker.homeowners.exception.HomeownersException;
import ru.zeker.homeowners.exception.IndicationsAlreadyTransmiited;
import ru.zeker.homeowners.mapper.MeterIndicationsMapper;
import ru.zeker.homeowners.mapper.MeterMapper;
import ru.zeker.homeowners.repository.MeterHistoryRepository;
import ru.zeker.homeowners.repository.MetersRepository;
import ru.zeker.homeowners.repository.PersonalAccountRepository;

/**
 * Сервис для управления историей показаний приборов учёта.
 * <p>
 * Отвечает за бизнес-логику:
 * <ul>
 *   <li>Передача новых показаний счетчика с сохранением в историю</li>
 *   <li>Получение истории показаний для всех счетчиков объекта недвижимости</li>
 *   <li>Формирование ответов с обогащёнными данными (счетчик + недвижимость + показания)</li>
 * </ul>
 * </p>
 * <p><strong>Важно:</strong> Все методы работают в рамках транзакции, управляемой вызывающим контроллером.
 * Валидация бизнес-правил (например, "показания не могут уменьшаться") должна выполняться
 * либо на уровне контроллера, либо добавляться в этот сервис.</p>
 *
 * @author Your Name
 * @version 1.0
 */
@RequiredArgsConstructor
@Service
public class MetersIndicationService {

  private final MeterHistoryRepository repository;
  private final MetersRepository metersRepository;
  private final PersonalAccountRepository personalAccountRepository;
  private final MeterIndicationsMapper mapper;
  private final MeterMapper meterMapper;
  private final UserProfileService userProfileService;

  /**
   * Добавление новых показаний для прибора учёта.
   *
   * <p><strong>Алгоритм работы:</strong></p>
   * <ol>
   *   <li>Поиск счетчика ({@code Meter}) по {@code request.meterId()}
   *       <ul>
   *         <li>Если не найден — выбрасывается {@code HomeownersException.meterNotFound()}</li>
   *       </ul>
   *       <ul>
   *    *    <li>Если подача показаний уже осуществлялась сегодня -  {@code HomeownersException.INdicationsAlreadyTransmitted()}</li>
   *    *  </ul>
   *
   *   </li>
   *   <li>Маппинг запроса ({@code MeterIndicationsRequest}) в сущность истории ({@code MeterHistoryValue})
   *       <ul>
   *         <li>Связь с {@code Meter} устанавливается внутри маппера</li>
   *       </ul>
   *   </li>
   *   <li>Загрузка лицевого счета ({@code PersonalAccount}) по ID, полученному из счетчика
   *       <ul>
   *         <li>Используется {@code .get()} без проверки {@code Optional} — если счет не найден,
   *             будет выброшено {@code NoSuchElementException}</li>
   *       </ul>
   *   </li>
   *   <li>Сохранение записи истории показаний в БД</li>
   *   <li>Формирование ответа через {@code MeterIndicationsResponse.of()} с данными:
   *       <ul>
   *         <li>Сохранённая запись истории ({@code MeterHistoryValue})</li>
   *         <li>Недвижимость ({@code Property}) из лицевого счета</li>
   *         <li>DTO счетчика ({@code MetersResponse}) через {@code MeterMapper}</li>
   *       </ul>
   *   </li>
   * </ol>
   *
   *
   * <p><strong>Возможные исключения:</strong></p>
   * <ul>
   *   <li>{@code HomeownersException} с кодом {@code METER_NOT_FOUND} — если счетчик не найден</li>
   *   <li>{@code java.util.NoSuchElementException} — если лицевой счет, привязанный к счетчику, удалён из БД
   *       (нарушение целостности данных)</li>
   *   <li>{@code org.springframework.dao.DataIntegrityViolationException} — при нарушении ограничений БД
   *       (уникальность, внешние ключи)</li>
   * </ul>
   *
   * @param request DTO с данными показаний: {@code meterId}, {@code value}, {@code indicationDate},
   *                опционально {@code comment}, {@code photoUrl}
   * @return DTO с информацией о сохранённых показаниях, включая данные счетчика и недвижимости
   * @throws HomeownersException если счетчик не найден
   * @throws HomeownersException если лицевой счет, связанный со счетчиком, не существует
   * @see MeterIndicationsMapper#toEntity(MeterIndicationsRequest, Meter)
   * @see MeterIndicationsResponse#of(MeterHistoryValue, Property,
   * ru.zeker.homeowners.domain.dto.response.MetersResponse)
   */
  public MeterIndicationsResponse addMeterIndications(MeterIndicationsRequest request) {
    Meter meter = metersRepository.findById(request.meterId())
        .orElseThrow(() -> HomeownersException.meterNotFound());
    MeterHistoryValue meterHistory = mapper.toEntity(request, meter);

    PersonalAccount personalAccount = personalAccountRepository
        .findById(meter.getPersonalAccount().getId())
        .get();
    try {
      MeterHistoryValue saved = repository.save(meterHistory);
    } catch (DataIntegrityViolationException e) {
      throw new IndicationsAlreadyTransmiited();
    }

    MeterIndicationsResponse response = MeterIndicationsResponse.of(
        meterHistory,
        personalAccount.getProperty(),
        meterMapper.toModel(meter)
    );
    return response;
  }

  /**
   * Получение истории показаний для всех счетчиков объекта недвижимости.
   *
   * <p><strong>Алгоритм работы:</strong></p>
   * <ol>
   *   <li>Поиск всех лицевых счетов ({@code PersonalAccount}) для {@code propertyId}</li>
   *   <li>Если счета не найдены — выбрасывается {@code HomeownersException.accountNotFound()}</li>
   *   <li>Для каждого лицевого счета:
   *     <ul>
   *       <li>Загружаются все привязанные счетчики ({@code Meter})</li>
   *       <li>Для каждого счетчика запрашиваются <strong>все</strong> записи истории
   *           через {@code repository.findByMeterId(meter.getId())}</li>
   *       <li>Если запись найдена (не {@code null}) — формируется ответ через {@code MeterIndicationsResponse.of()}</li>
   *     </ul>
   *   </li>
   *   <li>Все ответы агрегируются в список и возвращаются</li>
   * </ol>
   *
   *
   * <p><strong>Бизнес-правила:</strong></p>
   * <ul>
   *   <li>Возвращаются показания только для счетчиков, привязанных к лицевым счетам данной недвижимости</li>
   *   <li>Если у счетчика нет ни одной записи истории — он не включается в ответ</li>
   *   <li>Пустой список ({@code []}) возвращается, если у недвижимости нет счетчиков с историей
   *       (это не считается ошибкой)</li>
   * </ul>
   *
   * <p><strong>Возможные исключения:</strong></p>
   * <ul>
   *   <li>{@code HomeownersException} с кодом {@code ACCOUNT_NOT_FOUND} — если для {@code propertyId}
   *       не найдено ни одного лицевого счета</li>
   *   <li>{@code org.springframework.dao.DataAccessResourceFailureException} — при проблемах с подключением к БД</li>
   * </ul>
   *
   * @param propertyId UUID объекта недвижимости
   * @return список DTO с историей показаний (максимум одна запись на каждый счетчик)
   * @throws HomeownersException если не найдено ни одного лицевого счета для недвижимости
   * @see PersonalAccountRepository#findAllByPropertyId(UUID)
   * @see MetersRepository#findByPersonalAccountId(UUID)
   * @see MeterHistoryRepository#findByMeterId(UUID)
   */
  public List<MeterIndicationsResponse> getHistoryIndications(UUID propertyId, UUID accountId) {
    List<MeterIndicationsResponse> response = new ArrayList<>();
    UserProfileResponse userProfileData = userProfileService.getProfileResponse(accountId);
    boolean hasAccess = userProfileData.properties().stream()
        .anyMatch(property -> property.propertyId().equals(propertyId));

    if (!hasAccess) {
      throw HomeownersException.propertyNotFound();
    }
    List<PersonalAccount> personalAccounts = personalAccountRepository.findAllByPropertyId(
        propertyId);

    if (personalAccounts.isEmpty()) {
      throw HomeownersException.accountNotFound();
    }

    for (PersonalAccount personalAccount : personalAccounts) {
      List<Meter> meters = metersRepository.findByPersonalAccountId(personalAccount.getId());

      for (Meter meter : meters) {
        repository.findByMeterId(meter.getId())
            .forEach(history -> response.add(MeterIndicationsResponse.of(
                history,
                personalAccount.getProperty(),
                meterMapper.toModel(meter)
            )));
      }

    }
    return response;
  }
}

