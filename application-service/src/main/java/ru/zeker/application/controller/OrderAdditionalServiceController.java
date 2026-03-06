package ru.zeker.application.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zeker.application.domain.model.dto.request.OrderAdditionalServiceRequest;
import ru.zeker.application.domain.model.dto.response.application.AdditionalServiceInfoResponse;
import ru.zeker.application.domain.model.dto.response.application.OrderAdditionalServiceResponse;
import ru.zeker.application.service.OrderAdditionalService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(
        name = "OrderAdditionalServiceController",
        description = "A controller for working with additional service"
)
public class OrderAdditionalServiceController {
    OrderAdditionalService service;
    @PostMapping
    public ResponseEntity<OrderAdditionalServiceResponse> addAdditionalService(@RequestBody OrderAdditionalServiceRequest orderAdditionalServiceRequest,
                                                                               @RequestHeader UUID accountId){
        return ResponseEntity.ok(service.createOrder(accountId,orderAdditionalServiceRequest));

    }
    @GetMapping("/my")
    public ResponseEntity<List<OrderAdditionalServiceResponse>> getMyOrderAdditionalServices(@RequestHeader UUID accountId){
        return ResponseEntity.ok(service.getMyOrders(accountId));

    }
}

