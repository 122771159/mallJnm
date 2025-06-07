package com.jnm.mallJnm.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class AccountPasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String userType;
    private final String openId;
    public AccountPasswordAuthenticationToken(Object principal, Object credentials, String userType,String openId) {
        super(principal, credentials);
        this.userType = userType;
        this.openId = openId;
    }

    public AccountPasswordAuthenticationToken(Object principal, Object credentials, String userType, Collection<? extends GrantedAuthority> authorities,String openId) {
        super(principal, credentials, authorities);
        this.userType = userType;
        this.openId = openId;
    }

    public String getUserType() {
        return this.userType;
    }
    public String getOpenId(){
        return this.openId;
    }
}
