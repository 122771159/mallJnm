package com.jnm.mallJnm.util;

import com.jnm.mallJnm.exception.ValidatedException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class StringUtil {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String random(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        return sb.toString();
    }

    public static String listToSqlString(List<String> list) {
        if (list != null && !list.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (String s : list) {
                result.append(",'").append(s).append("'");
            }
            return result.substring(1);
        } else {
            return "";
        }
    }

    public static String createNo() {
        String no = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, 4);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return no + fixLenthString.substring(1, 5);
    }
    public static String createSortStr(String str1,String str2){
        int result = str1.compareTo(str2);
        StringBuilder sb = new StringBuilder();
        if (result < 0) {
           sb.append(str1).append(str2);
        } else if (result > 0) {
            sb.append(str2).append(str1);
        } else {
            throw new ValidatedException(-1, "排序字符串不能相同");
        }
        return sb.toString();
    }
    public static Map<String, String> getMapSortStr(String str, String str2) {
        Map<String, String> map = new HashMap<>();
        int result = str.compareTo(str2);
        if (result < 0) {
            map.put("min", str);
            map.put("max", str2);
        } else if (result > 0) {
            map.put("min", str2);
            map.put("max", str);
        } else {
            throw new ValidatedException(-1, "排序字符串不能相同");
        }
        return map;
    }
}
