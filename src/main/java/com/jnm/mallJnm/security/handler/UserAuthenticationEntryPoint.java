package com.jnm.mallJnm.security.handler;

import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.util.ResponseUtil;
import com.jnm.mallJnm.controller.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Result result = Result.fail(ErrorEnum.NOT_LOGIN.getCode(), ErrorEnum.NOT_LOGIN.getMsg());
        ResponseUtil.outOfJson(response, result);
    }
}
