package com.jnm.mallJnm.security.filter;

import com.jnm.mallJnm.controller.result.Result;
import com.jnm.mallJnm.security.token.JwtAuthenticationToken;
import com.jnm.mallJnm.util.ResponseUtil;
import com.jnm.mallJnm.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RequestMatcher requiresAuthenticationRequestMatcher;
    private AuthenticationManager authenticationManager;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    public JwtAuthenticationFilter() {
        this.requiresAuthenticationRequestMatcher = new RequestHeaderRequestMatcher("Authorization");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!requiresAuthentication(request)) {
            chain.doFilter(request, response);
        } else {
            String authInfo = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(authInfo) && authInfo.startsWith("Bearer ")) {
                String token = authInfo.substring(7);
                if (!token.isEmpty()) {
                    try {
                        if (TokenUtil.isExpiration(token)) {
                            unsuccessfulAuthentication(response, "Token已过期");
                            return;
                        }
                        Claims claims = TokenUtil.getTokenClaims(token);
                        String userId = claims.getId();
                        String userType = (String) claims.get("userType");
                        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(userId, null, userType);
                        this.setDetails(request, authenticationToken);
                        Authentication authResult = this.getAuthenticationManager().authenticate(authenticationToken);
                        if (authResult != null) {
                            successfulAuthentication(response, authResult, claims);
                        }
                    } catch (AuthenticationException e) {
                        unsuccessfulAuthentication(response, e.getMessage());
                        return;
                    } catch (JwtException e){
                        unsuccessfulAuthentication(response, "Token错误");
                        return;
                    }
                }
            }
            chain.doFilter(request, response);
        }
    }

    private void successfulAuthentication(HttpServletResponse response, Authentication authResult, Claims claims) {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        if (System.currentTimeMillis() > claims.getIssuedAt().getTime() + 1000 * 60 * 60 * 24 * 7) {
            String newToken = TokenUtil.createToken(claims.getId(), claims.getSubject(), (String) claims.get("userType"));
            response.setHeader("Authorization", newToken);
        }
    }

    private void unsuccessfulAuthentication(HttpServletResponse response, String message) {
//        response.setStatus(HttpStatus.BAD_REQUEST.value());
        Result result = Result.fail(104, message);
        ResponseUtil.outOfJson(response, result);
    }

    private void setDetails(HttpServletRequest request, JwtAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return this.requiresAuthenticationRequestMatcher.matches(request);
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }
}
