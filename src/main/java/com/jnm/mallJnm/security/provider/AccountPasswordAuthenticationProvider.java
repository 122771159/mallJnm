package com.jnm.mallJnm.security.provider;

import com.jnm.mallJnm.security.token.AccountPasswordAuthenticationToken;
import com.jnm.mallJnm.service.LoginService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;


public class AccountPasswordAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private LoginService loginService;
    private PasswordEncoder passwordEncoder;
    private volatile String userNotFoundEncodedPassword;

    @Override
    protected void doAfterPropertiesSet() {
        Assert.notNull(this.loginService, "A LoginService must be set");
    }
    // 判断密码是否正确
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "密码错误"));
        } else {
                String presentedPassword = authentication.getCredentials().toString();
                if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                    throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials",
                            "密码错误"));
                }

        }
    }
    // 检索用户
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        this.prepareTimingAttackProtection();

        try {
            AccountPasswordAuthenticationToken token = (AccountPasswordAuthenticationToken) authentication;
            // 根据用户名查询用户，并返回UserDetails
            String userType = token.getUserType();
            String openId = token.getOpenId();
            UserDetails loadedUser;
            if("".equals(userType)){
                loadedUser = loginService.loadByAccount(username,openId);
            }else{
                loadedUser = loginService.loadByAccountType(username, token.getUserType(),openId);
            }
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
            } else {
                return loadedUser;
            }
        } catch (UsernameNotFoundException e) {
            this.mitigateAgainstTimingAttack(authentication);
            throw e;
        } catch (InternalAuthenticationServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccountPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        //todo 判断密码强度是否低于当前设置，若低于当前则重新加密密码并更新（可参考DaoAuthenticationProvider）
//        AccountPasswordAuthenticationToken token = (AccountPasswordAuthenticationToken) authentication;
//        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
//        this.loginService.updateLoginInfo(((User) user).getId(), token.getUserType(), details.getRemoteAddress());
        return super.createSuccessAuthentication(principal, authentication, user);
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode("userNotFoundPassword");
        }
    }

    private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    protected PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.userNotFoundEncodedPassword = null;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
}
