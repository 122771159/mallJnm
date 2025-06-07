package com.jnm.mallJnm.security.provider;

import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.token.JwtAuthenticationToken;
import com.jnm.mallJnm.service.LoginService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;


public class JwtAuthenticationProvider implements AuthenticationProvider, InitializingBean {
    @Autowired
    private LoginService loginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new IllegalArgumentException("Only JwtAuthenticationToken is supported");
        }
        String userId = authentication.getName();
        String userType = token.getUserType();
        User user = loginService.loadById(userId, userType);
        if (user == null) {
            throw new InternalAuthenticationServiceException("未找到此用户");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("用户已禁用");
        }
        JwtAuthenticationToken result = new JwtAuthenticationToken(user, authentication.getCredentials(), userType, user.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return JwtAuthenticationToken.class.isAssignableFrom(aClass);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.loginService, "A LoginService must be set");
    }
}
