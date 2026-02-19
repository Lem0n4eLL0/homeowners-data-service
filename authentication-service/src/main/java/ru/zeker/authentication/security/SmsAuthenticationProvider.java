package ru.zeker.authentication.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.zeker.authentication.domain.model.entity.Account;
import ru.zeker.authentication.service.OtpService;

@RequiredArgsConstructor
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final OtpService otpService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        var phone = (String) authentication.getPrincipal();
        var code = (String) authentication.getCredentials();

        otpService.verify(phone, code);

        var user = (Account) userDetailsService.loadUserByUsername(phone);

        return new SmsAuthenticationToken(user, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
