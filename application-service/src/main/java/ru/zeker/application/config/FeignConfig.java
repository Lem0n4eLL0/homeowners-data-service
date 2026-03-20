package ru.zeker.application.config;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.zeker.common.headers.AppHeaders;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String accountId = request.getHeader(AppHeaders.ACCOUNT_ID);
            if (accountId != null && !accountId.isEmpty()) {
                template.header(AppHeaders.ACCOUNT_ID, accountId);
                log.debug("Feign: пробрасываем Account-Id={}", accountId);
            }

            String auth = request.getHeader("Authorization");
            if (auth != null && !auth.isEmpty()) {
                template.header("Authorization", auth);
            }
            String requestId = request.getHeader("X-Request-ID");
            if (requestId != null && !requestId.isEmpty()) {
                template.header("X-Request-ID", requestId);
            }
        }
    }
}