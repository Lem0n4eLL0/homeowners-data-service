package ru.zeker.authentication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.zeker.common.config.JwtProperties;
import ru.zeker.common.util.JwtUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
public class AuthenticationBeansConfig {

    @Value("${app.security.otp.hmac-secret}")
    private String secretPath;

    @Bean
    public String otpHmacSecret() throws IOException {
        if (secretPath.startsWith("/")) {
            return Files.readString(Path.of(secretPath)).trim();
        }
        return secretPath;
    }

    @Bean
    public JwtUtils jwtUtils(JwtProperties jwtProperties) {
        return new JwtUtils(jwtProperties);
    }

}
