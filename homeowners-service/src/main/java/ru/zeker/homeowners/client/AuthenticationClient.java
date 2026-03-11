package ru.zeker.homeowners.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.zeker.common.dto.request.EmailRequest;
import ru.zeker.common.dto.response.AccountResponse;
import ru.zeker.common.headers.AppHeaders;

import java.util.UUID;

@FeignClient(
        name = "authentication-service",
        url = "${authentication.service.url}"
)
public interface AuthenticationClient {

    @GetMapping("/me")
    ResponseEntity<AccountResponse> getAccount(
            @RequestHeader(AppHeaders.ACCOUNT_ID) UUID accountId
    );

    @PostMapping("/email/request")
    void requestEmailVerification(
            @RequestHeader(AppHeaders.ACCOUNT_ID) UUID accountId,
            @RequestBody EmailRequest request
    );
}