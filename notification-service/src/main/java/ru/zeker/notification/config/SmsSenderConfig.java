package ru.zeker.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.dezhik.sms.sender.SenderService;
import ru.dezhik.sms.sender.SenderServiceConfiguration;
import ru.dezhik.sms.sender.SenderServiceConfigurationBuilder;

@Configuration
public class SmsSenderConfig {

    @Value("${app.sms.api-id}")
    private String apiId;

    @Bean
    @Profile("prod")
    SenderServiceConfiguration prodSenderServiceConfiguration() {
        return SenderServiceConfigurationBuilder.create()
                .setApiId(apiId)
                .build();
    }

    @Bean
    @Profile("dev")
    SenderServiceConfiguration devSenderServiceConfiguration() {
        return SenderServiceConfigurationBuilder.create()
                .setApiId(apiId)
                .setTestSendingEnabled(true)
                .setReturnPlainResponse(true)
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    SenderService senderService(SenderServiceConfiguration configuration) {
        return new SenderService(configuration);
    }
}
