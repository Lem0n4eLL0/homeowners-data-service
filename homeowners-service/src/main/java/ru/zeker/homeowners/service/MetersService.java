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

    for(PersonalAccount personalAccount:personalAccounts){
      List<Meter> meters = repository.findByPersonalAccountId(personalAccount.getId());
      response.addAll(mapper.toModel(meters));
    }
    return response;
  }

  public MetersResponse addMeter(MeterRequest request){
    log.info("""
     [METER_CREATE] Incoming request:
       • personalAccountId: {}
       • type: {}
       • serialNumber: {}
       • raw request: {}
    """,
        request.personalAccountId(),
        request.type(),
        request.serialNumber(),
        request
    );
    PersonalAccount personalAccount = personalAccountRepository.findById(request.personalAccountId()).get();
    Meter meter = repository.save(mapper.toEntity(request,personalAccount));
    log.info("Mapped entity: serialNumber={}, type={}, personalAccount={}",
        meter.getSerialNumber(), meter.getType(), meter.getPersonalAccount());
    return mapper.toModel(meter);

  }




}
