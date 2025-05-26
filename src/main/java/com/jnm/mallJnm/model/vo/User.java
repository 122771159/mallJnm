package com.jnm.mallJnm.model.vo;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class User implements UserDetails, CredentialsContainer {
    private final String id;

    private final String account;
    private final String name;
    private String password;
    private final String userType;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonExpired; //账户没有过期
    private final boolean accountNonLocked; //账户没被锁定 （是否冻结）
    private final boolean credentialsNonExpired; //密码没有过期
    private final boolean enabled; //账户是否可用（是否被删除）

    public User(String id, String account, String name, String password, String userType, Collection<? extends GrantedAuthority> authorities) {
        this(id, account, name, password, userType, authorities, true, true, true, true);
    }

    public User(String id, String account, String name, String password, String userType, Collection<? extends GrantedAuthority> authorities, boolean enabled) {
        this(id, account, name, password, userType, authorities, true, true, true, enabled);
    }

    public User(String id, String account, String name, String password, String userType, Collection<? extends GrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        this.id = id;
        this.account = account;
        this.name = name;
        this.password = password;
        this.userType = userType;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.account;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public String getId() {
        return this.id;
    }

    public String getAccount() {
        return this.account;
    }

    public String getName() {
        return this.name;
    }

    public String getUserType() {
        return this.userType;
    }
}
