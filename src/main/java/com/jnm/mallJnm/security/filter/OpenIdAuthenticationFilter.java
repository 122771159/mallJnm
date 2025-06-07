package com.jnm.mallJnm.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnm.mallJnm.controller.result.DataResult;
import com.jnm.mallJnm.controller.result.Result;
import com.jnm.mallJnm.exception.VerifyException;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.token.OpenIdAuthenticationToken;
import com.jnm.mallJnm.util.ResponseUtil;
import com.jnm.mallJnm.util.StringUtil;
import com.jnm.mallJnm.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenIdAuthenticationFilter extends OncePerRequestFilter {
    private final RequestMatcher requiresAuthenticationRequestMatcher;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private AuthenticationManager authenticationManager;
    public OpenIdAuthenticationFilter(){
        this.requiresAuthenticationRequestMatcher = new AntPathRequestMatcher("/wxlogin","POST");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!requiresAuthentication(request)){
            filterChain.doFilter(request, response);
        }else{
            BufferedReader reader = request.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            String jsonData = requestBody.toString();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(jsonData, Map.class);
            String openId = data.get("openId");
            String userType = data.get("userType");
            if(StringUtil.isNullOrEmpty(openId)){
                throw new VerifyException("OPENID不可为空");
            }
            if(StringUtil.isNullOrEmpty(userType)){
                throw new VerifyException("用户类型不能为空");
            }
            OpenIdAuthenticationToken openIdAuthenticationToken = new OpenIdAuthenticationToken(openId,"",userType);
            this.setDetails(request, openIdAuthenticationToken);
            try{
                Authentication authResult = this.getAuthenticationManager().authenticate(openIdAuthenticationToken);
                if (authResult != null) {
                    successfulAuthentication(response, authResult);
                }
            } catch (InternalAuthenticationServiceException failed) {
                this.logger.error("An internal error occurred while trying to authenticate the user.", failed);
                this.unsuccessfulAuthentication(request, response, failed);
            } catch (AuthenticationException ex) {
                this.unsuccessfulAuthentication(request, response, ex);
            }

        }
    }
    private void successfulAuthentication(HttpServletResponse response, Authentication authResult) {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        User user = (User) authResult.getPrincipal();
        String token;
        token = TokenUtil.createToken(user.getId(), user.getName(), user.getUserType());
        Map<String, Object> data = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        data.put("token", token);
        data.put("user", map);
        data.put("roles", user.getUserType());
        data.put("tokenExpiresIn", TokenUtil.tokenExpiration);
        data.put("tokenFreeTimeout", TokenUtil.tokenFreeTimeout);
        DataResult<Map<String, Object>> result = new DataResult<>(data);
        ResponseUtil.outOfJson(response, result);
    }
    private boolean requiresAuthentication(HttpServletRequest request) {
        return this.requiresAuthenticationRequestMatcher.matches(request);
    }
    private void setDetails(HttpServletRequest request, OpenIdAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        Result result = Result.fail(104, failed.getMessage());
        ResponseUtil.outOfJson(response, result);
    }
}
