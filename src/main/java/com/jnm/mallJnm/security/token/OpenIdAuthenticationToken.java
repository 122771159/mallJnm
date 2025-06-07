package com.jnm.mallJnm.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class OpenIdAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String userType;
    public OpenIdAuthenticationToken(Object principal, Object credentials, String userType) {
        super(principal, credentials);
        this.userType = userType;
    }

    public OpenIdAuthenticationToken(Object principal, Object credentials, String userType, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }
    public String getOpenId(){
        return getName();
    }
}
