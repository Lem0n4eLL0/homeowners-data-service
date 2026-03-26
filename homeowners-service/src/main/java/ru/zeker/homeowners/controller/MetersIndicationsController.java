package ru.zeker.homeowners.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.homeowners.domain.dto.request.MeterIndicationsRequest;
import ru.zeker.homeowners.domain.dto.response.MeterIndicationsResponse;
import ru.zeker.homeowners.service.MetersIndicationService;

@RestController
@RequestMapping("/meters/indications")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "MetersIndications API", description = "Контроллер для работы с историей показаний")
public class MetersIndicationsController {

  private final MetersIndicationService service;
@PostMapping
  public ResponseEntity<MeterIndicationsResponse> addMeterIndications(@RequestBody
      MeterIndicationsRequest request){
    return ResponseEntity.ok(service.addMeterIndications(request));

  }
@GetMapping("property/{propertyId}")
  public ResponseEntity<List<MeterIndicationsResponse>> getHistoryIndications(@PathVariable("propertyId")  UUID propertyId){
      return ResponseEntity.ok(service.getHistoryIndications(propertyId));
  }

}
