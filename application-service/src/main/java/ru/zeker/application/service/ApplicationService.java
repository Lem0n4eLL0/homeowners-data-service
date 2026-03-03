package ru.zeker.application.service;

import org.springframework.stereotype.Service;
import ru.zeker.application.domain.model.dto.response.application.ApplicationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {
    public List<ApplicationResponse> getByAccountId(UUID accountId){
        return new ArrayList<ApplicationResponse>();
    }
}
