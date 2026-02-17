package ru.zeker.authentication.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal; // phone
    private Object credentials;     // code

    public SmsAuthenticationToken(String phone, String code) {
        super(null);
        this.principal = phone;
        this.credentials = code;
        setAuthenticated(false);
    }

    public SmsAuthenticationToken(UserDetails user,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = user;
        this.credentials = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}