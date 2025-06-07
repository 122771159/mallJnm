package com.jnm.mallJnm.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;


public class TokenUtil {
//    public static final long tokenExpiration = 24 * 3600 * 1000 * 7; // 7天
//    public static final long tokenExpiration = 250 * 60 * 16; // 4分钟
    public static final long tokenExpiration = 250 * 1000; // 250 秒
//    public static final long tokenFreeTimeout = 2 * 60 * 60 * 1000; // 2小时
//    public static final long tokenFreeTimeout = 1000 * 60 * 15; // 15 分钟
    public static final long tokenFreeTimeout = 1000 * 300; // 15 分钟
//    public static final long tokenFreeTimeout = 0; // 0
    private static final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("65439153e2284f64a7743788d82d8b02d26c54834f91"));

    public static String createToken(String id, String name, String userType) {
        return Jwts.builder().header().add("typ", "JWT")
                .add("alg", "HS256").and().issuedAt(new Date())
                .id(id).subject(name).claim("userType", userType)
                .expiration(new Date(System.currentTimeMillis()+tokenExpiration))
                .signWith(key).compact();
    }
    public static String createToken(String id, String name, String userType,Boolean isRemember) {
        return Jwts.builder().header().add("typ", "JWT")
                .add("alg", "HS256").and().issuedAt(new Date())
                .id(id).subject(name).claim("userType", userType)
                .expiration(new Date(System.currentTimeMillis()+ 365L *24*3600*1000*10))
                .signWith(key).compact();
    }

    public static String getIdByToken(String token) {
        return getTokenClaims(token).getId();
    }

    public static boolean isExpiration(String token){
        return getTokenClaims(token).getExpiration().before(new Date());
    }

    public static Claims getTokenClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }
        return claims;
    }
}
