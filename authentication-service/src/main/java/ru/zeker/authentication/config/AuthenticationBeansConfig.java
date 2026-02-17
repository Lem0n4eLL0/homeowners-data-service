package ru.zeker.authentication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.zeker.authentication.security.SmsAuthenticationProvider;
import ru.zeker.authentication.service.AccountService;
import ru.zeker.authentication.service.OtpService;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtUtils jwtUtils(JwtProperties jwtProperties) {
        return new JwtUtils(jwtProperties);
    }

    @Bean
    public UserDetailsService userDetailsService(AccountService userService) {
        return userService::findOrCreateByPhone;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, OtpService otpService) {
        return new SmsAuthenticationProvider(userDetailsService, otpService);
    }
}
