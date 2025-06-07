package com.jnm.mallJnm.security.utils;

import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.token.JwtAuthenticationToken;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {
    public User getCurrentUser() {
        try{
            JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return (User) authentication.getPrincipal();
        } catch (Exception e) {
            throw new ServerException(ErrorEnum.NOT_LOGIN);
        }

    }
}
