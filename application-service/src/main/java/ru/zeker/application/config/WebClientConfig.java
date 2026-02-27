package ru.zeker.application.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.homeowners.url:http://homeowners-service:8082}")
    private String homeownersUrl;

    @Bean
    public WebClient homeownersWebClient() {
        return WebClient.builder()
                .baseUrl(homeownersUrl)
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
                )
                .build();
    }


}