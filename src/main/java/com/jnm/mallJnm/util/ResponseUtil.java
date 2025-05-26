package com.jnm.mallJnm.util;

import com.jnm.mallJnm.controller.result.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


@Slf4j
public class ResponseUtil {
    public static void outOfJson(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println(JSONUtil.toJSONString(result));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    public static String requestGet(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }

    public static Map<?,?> requestPostObject(String url, Map<String, Object> data) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
        try {
            return restTemplate.postForObject(url, request, Map.class);
        } catch (HttpClientErrorException e) {
            log.info("请求失败", e);
            throw e;
        }
    }

    public static ResponseEntity<String> requestPostEntity(String url, Map<String, Object> data) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
        try {
            return restTemplate.postForEntity(url, request, String.class);
        } catch (RestClientException e) {
            log.info("请求失败", e);
            throw e;
        }
    }

    public static ResponseEntity<String> requestPost(String url, Map<String, String> headerMap, Map<String, Object> data) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(headerMap);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
        try {
            return restTemplate.postForEntity(url, request, String.class);
        } catch (HttpClientErrorException e) {
            log.info("请求失败", e);
            throw e;
        }
    }
}
