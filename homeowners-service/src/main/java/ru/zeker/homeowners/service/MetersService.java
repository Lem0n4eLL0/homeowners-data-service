package ru.zeker.homeowners.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zeker.homeowners.domain.dto.request.MeterRequest;
import ru.zeker.homeowners.domain.dto.response.MetersResponse;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.domain.model.enums.ServiceCode;
import ru.zeker.homeowners.exception.HomeownersException;
import ru.zeker.homeowners.mapper.MeterMapper;
import ru.zeker.homeowners.repository.MetersRepository;
import ru.zeker.homeowners.repository.PersonalAccountRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class MetersService {
  private final MetersRepository repository;
  private final PersonalAccountRepository personalAccountRepository;
  private final MeterMapper mapper;

  public List<MetersResponse> getMeters(UUID propertyId){
    List<MetersResponse> response = new ArrayList<>();
     List<PersonalAccount> personalAccounts = personalAccountRepository.findAllByPropertyId(propertyId);

    if(personalAccounts.isEmpty()) throw HomeownersException.accountNotFound();

    for(PersonalAccount personalAccount:personalAccounts){
      List<Meter> meters = repository.findByPersonalAccountId(personalAccount.getId());
      response.addAll(mapper.toModel(meters));
    }
    return response;
  }

  public MetersResponse addMeter(MeterRequest request){
    log.info("Сохранение счетчика");
    ServiceCode serviceCode =ServiceCode.valueOf(request.type().name());
    PersonalAccount personalAccount = personalAccountRepository.findByPropertyIdAndServiceCode(request.propertyId(),serviceCode).get();
    Meter meter = repository.save(mapper.toEntity(request,personalAccount));
    return mapper.toModel(meter);

  }






}
