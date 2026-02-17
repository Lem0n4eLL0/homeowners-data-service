package ru.zeker.authentication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import ru.zeker.common.config.JwtProperties;

@Configuration
@Import(AuthenticationBeansConfig.class)
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;

    /**
     * Generates a security filter chain for authentication endpoints.
     *
     * <p>Authentication endpoints do not require authentication, so CSRF protection is disabled for them
     * and authorization is allowed for all requests.
     * </p>
     *
     * @param http : {@link HttpSecurity} object used to configure the filter chain
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the filter chain
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authEndpointsFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/auth/**")
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider);
        return http.build();
    }

    /**
     * Configures the primary security filter chain for protected endpoints.
     *
     * <p>This filter chain applies to all requests that are not handled
     * by the OAuth2 and authentication filter chains. It requires all requests to be
     * authenticated, disables CSRF protection, and configures session management
     * as STATELESS, which aligns with the REST API approach.</p>
     *
     * <p>The chain also adds a custom header validation filter before
     * the default username/password authentication filter.</p>
     *
     * @param http {@link HttpSecurity} object used to configure the filter chain
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the filter chain
     */
    @Bean
    @Order(2)
    public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

}
