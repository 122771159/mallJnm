package com.jnm.mallJnm.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
@Component
public class WechatUtil {
    //	配置自己的app_id、app_secret
    @Value("${wx.miniapp.appid}")
    private String APP_ID;
    @Value("${wx.miniapp.secret}")
    private String APP_SECRET;

    public String getOpenId(String loginCode) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        String requestUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("appid", APP_ID)
                .queryParam("secret", APP_SECRET)
                .queryParam("js_code", loginCode)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        HttpResponse response = HttpUtil.createGet(requestUrl).execute();

        // 获取 session_key 和 openid
        JSONObject parseObj = JSONUtil.parseObj(response.body());

        return (String) parseObj.get("openid");

    }
}