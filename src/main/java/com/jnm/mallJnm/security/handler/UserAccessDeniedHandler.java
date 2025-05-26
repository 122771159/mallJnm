package com.jnm.mallJnm.security.handler;

import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.util.ResponseUtil;
import com.jnm.mallJnm.controller.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class UserAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Result result = Result.fail(ErrorEnum.NO_AUTHORITY.getCode(), ErrorEnum.NO_AUTHORITY.getMsg());
        ResponseUtil.outOfJson(response, result);
    }
}
