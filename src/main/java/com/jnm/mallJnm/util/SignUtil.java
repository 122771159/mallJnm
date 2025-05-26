package com.jnm.mallJnm.util;

public class SignUtil {
    private final static String yuanShuFormalToken = "343059c5-cd1a-4ade-b87b-1be92ce65caf";//元数正式token

    public static String createYuanShuSign(String platformNum, long timeStamp) {
        String value = ("key=" + yuanShuFormalToken + "&_platform_num=" + platformNum + "&timeStamp=" + timeStamp).toUpperCase();
        String signStr = MD5Util.encode(value, "UTF-8");
        return signStr.toUpperCase();
    }
}
