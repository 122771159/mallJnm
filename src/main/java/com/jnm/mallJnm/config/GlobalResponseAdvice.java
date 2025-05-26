package com.jnm.mallJnm.config;

import com.jnm.mallJnm.util.JSONUtil;
import com.jnm.mallJnm.controller.result.DataResult;
import com.jnm.mallJnm.controller.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;


@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 判定哪些请求要执行beforeBodyWrite
     * @param methodParameter 请求执行的方法
     * @param converterType 信息转化类
     * @return 是否执行
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        //获取当前处理请求的controller的方法
//        Method method = methodParameter.getMethod();
//        if (method != null) {
//            String methodName = method.getName();
//            return !"getImage".equals(methodName);
//        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //无返回值，返回成功
        if (body == null) {
            return Result.success();
        }
        //返回为资源或已封装则直接返回
        if (body instanceof Resource || body instanceof Result) {
            return body;
        }
        if (body instanceof String) {
            return JSONUtil.toJSONString(new DataResult<>(body));
        }
        if ("error".equals(methodParameter.getMethod().getName())) {
            Map<String, Object> map = (Map<String, Object>) body;
            return Result.fail((int) map.get("status"), map.get("error") + ":" + map.get("message"));
        }
        return new DataResult<>(body);
    }
}
