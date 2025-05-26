package com.jnm.mallJnm.security.utils;

import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.token.JwtAuthenticationToken;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {
    public User getCurrentUser() {
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
