package ru.zeker.homeowners.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zeker.homeowners.domain.dto.request.MeterIndicationsRequest;
import ru.zeker.homeowners.domain.dto.response.MeterIndicationsResponse;
import ru.zeker.homeowners.domain.model.entity.Meter;
import ru.zeker.homeowners.domain.model.entity.MeterHistoryValue;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.mapper.MeterIndicationsMapper;
import ru.zeker.homeowners.mapper.MeterMapper;
import ru.zeker.homeowners.repository.MeterHistoryRepository;
import ru.zeker.homeowners.repository.MetersRepository;
import ru.zeker.homeowners.repository.PersonalAccountRepository;

@RequiredArgsConstructor
@Service
public class MetersIndicationService {
  private final MeterHistoryRepository repository;
  private final MetersRepository metersRepository;
  private final PersonalAccountRepository personalAccountRepository;
  private final MeterIndicationsMapper mapper;
  private final MeterMapper meterMapper;

  public MeterIndicationsResponse addMeterIndications(MeterIndicationsRequest request){

    MeterHistoryValue meterHistory = mapper.toEntity(request);
    Meter meter = metersRepository.findById(request.meterId()).get();
    meterHistory.setMeter(meter);
    meterHistory.setDate(LocalDate.now());

    PersonalAccount personalAccount = personalAccountRepository.findById(meter.getPersonalAccount().getId()).get();
    MeterHistoryValue saved =  repository.save(meterHistory);
    MeterIndicationsResponse response = MeterIndicationsResponse.of(meterHistory,personalAccount.getProperty(),meterMapper.toModel(meter));
    return response;

  }

  public List<MeterIndicationsResponse> getHistoryIndications(UUID propertyId){
    List<MeterIndicationsResponse> response = new ArrayList<>();
    List<PersonalAccount> personalAccounts = personalAccountRepository.findAllByPropertyId(propertyId);
    for(PersonalAccount personalAccount:personalAccounts){
      List<Meter> meters = metersRepository.findByPersonalAccountId(personalAccount.getId());
      for(Meter meter:meters){
        MeterHistoryValue meterHistoryValue = repository.findByMeterId(meter.getId());
        if (!Objects.isNull(meterHistoryValue)) response.add(MeterIndicationsResponse.of(meterHistoryValue,personalAccount.getProperty(),meterMapper.toModel(meter)));

      }

    }
    return response;

  }


}
