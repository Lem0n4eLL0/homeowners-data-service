package ru.zeker.application.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceInfoResponse;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceResponse;
import ru.zeker.application.domain.model.entity.AdditionalServiceEntity;
import ru.zeker.application.domain.model.mapper.AdditionalServiceInfoMapper;
import ru.zeker.application.domain.model.mapper.AdditionalServiceMapper;
import ru.zeker.application.exceptions.ResourceNotFoundException;
import ru.zeker.application.repository.AdditionalServiceRepository;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@Service
public class AdditionalService {

    private final AdditionalServiceRepository additionalServiceRepository;
    private final AdditionalServiceMapper mapper;
    private final AdditionalServiceInfoMapper infoMapper;

    public List<AdditionalServiceResponse> getAllAdditionalServices(){
        List<AdditionalServiceEntity> additionalServiceEntities = additionalServiceRepository.findAll();
        return mapper.toModelList(additionalServiceEntities);
    }

    public AdditionalServiceInfoResponse getAdditionalService(UUID additionalServiceId){
        AdditionalServiceEntity additionalService = additionalServiceRepository.findById(additionalServiceId)
                .orElseThrow(() -> new ResourceNotFoundException(additionalServiceId));
        return infoMapper.toModel(additionalService);

    }

}
