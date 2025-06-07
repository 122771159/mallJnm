package com.jnm.mallJnm.security.provider;

import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.token.OpenIdAuthenticationToken;
import com.jnm.mallJnm.service.LoginService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;


public class OpenIdAuthenticationProvider implements AuthenticationProvider, InitializingBean {
    @Autowired
    private LoginService loginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof OpenIdAuthenticationToken token)) {
            throw new IllegalArgumentException("Only OpenIdAuthenticationToken is supported");
        }
        String openid = token.getOpenId();
        String userType = token.getUserType();
        User user = loginService.loadByOpenId(openid, userType);
        if (user == null) {
            throw new InternalAuthenticationServiceException("未找到此用户");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("用户已禁用");
        }
        OpenIdAuthenticationToken result = new OpenIdAuthenticationToken(user, authentication.getCredentials(), userType, user.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return OpenIdAuthenticationToken.class.isAssignableFrom(aClass);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.loginService, "A LoginService must be set");
    }
}
