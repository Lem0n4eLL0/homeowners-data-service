package ru.zeker.homeowners.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zeker.homeowners.domain.dto.request.MeterRequest;
import ru.zeker.homeowners.domain.dto.response.MetersResponse;
import ru.zeker.homeowners.service.MetersService;

@RestController
@RequestMapping("/meters")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Meters API", description = "Контроллер для работы с счетчиками")
public class MetersController {
  private final MetersService service;

  @GetMapping("/property/{propertyId}")
  public ResponseEntity<List<MetersResponse>> getMeters(@PathVariable("propertyId") @NotNull(message = "Property ID is required")   UUID propertyId){
    return ResponseEntity.ok(service.getMeters(propertyId));

  }

  @PostMapping
  public ResponseEntity<MetersResponse> addMeter(@Validated @RequestBody MeterRequest request){
    return ResponseEntity.ok(service.addMeter(request));
  }




}
