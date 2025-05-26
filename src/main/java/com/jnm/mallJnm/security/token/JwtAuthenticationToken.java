package com.jnm.mallJnm.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String userType;

    public JwtAuthenticationToken(Object principal, Object credentials, String userType) {
        super(principal, credentials);
        this.userType = userType;
    }

    public JwtAuthenticationToken(Object principal, Object credentials, String userType, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.userType = userType;
    }

    public boolean containsAuthority(String role) {
        if (this.getAuthorities() != null && !this.getAuthorities().isEmpty()) {
            for (GrantedAuthority authority : this.getAuthorities()) {
                if (authority.getAuthority().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getUserType() {
        return userType;
    }
}
