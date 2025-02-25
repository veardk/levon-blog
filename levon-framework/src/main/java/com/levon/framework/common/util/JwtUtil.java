package com.levon.framework.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.Calendar;

public class JwtUtil {

    // 私钥或公钥，用于签名和验证
    private static final String SECRET_KEY = "Levoniunafasdfuandnsnnasca";  // 请使用更安全的密钥
    private static final long EXPIRATION_TIME = 86400000L; // 24小时过期时间（单位毫秒）

    // 生成 JWT Token
    public static String generateToken(String username) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);  // 设置 24 小时过期

        return JWT.create()
                .withSubject(username)  // 设置 JWT 的主体（用户名等）
                .withIssuedAt(new Date())  // 设置生成时间
                .withExpiresAt(calendar.getTime())  // 设置过期时间
                .sign(Algorithm.HMAC256(SECRET_KEY));  // 使用 HMAC256 算法进行签名
    }

    // 解析 JWT Token
    public static DecodedJWT parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token);
    }

    // 获取 Token 中的用户名（或其他自定义信息）
    public static String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();  // 获取 sub 字段
    }

    // 获取 Token 中的过期时间
    public static Date getExpirationDateFromToken(String token) {
        return parseToken(token).getExpiresAt();  // 获取过期时间
    }

    // 检查 Token 是否过期
    public static boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    // 验证 Token 是否有效
    public static boolean validateToken(String token, String username) {
        return (username.equals(getUsernameFromToken(token)) && !isTokenExpired(token));
    }

    // 刷新 Token
    public static String refreshToken(String token) {
        String username = getUsernameFromToken(token);
        return generateToken(username);  // 重新生成新的 token，保留用户名信息
    }

    // 获取 JWT 生成的有效期（单位：毫秒）
    public static long getTokenValidity(String token) {
        return getExpirationDateFromToken(token).getTime() - new Date().getTime();
    }

    public static void main(String[] args) {
        // 生成 Token 示例
        String token = generateToken("testUser");
        System.out.println("Generated Token: " + token);

        String token2 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzQwMzA0MzY4LCJpYXQiOjE3NDAyMTc5Njh9.qskLMqj6ReDnbwTRTdROUPqHlq7gZRwrRqJ0SVAUdis";
        String name2 = getUsernameFromToken(token2);
        System.out.println("name2 = " + name2);
        String token3 = generateToken("1");
        String token4 = generateToken("1");
        System.out.println("token3 = " + token3);
        System.out.println("token4 = " + token4);
        // 解析 Token 示例
        String username = getUsernameFromToken(token);
        System.out.println("Username from Token: " + username);

        // 验证 Token 是否有效
        boolean isValid = validateToken(token, "testUser");
        System.out.println("Is Token Valid: " + isValid);

        // 刷新 Token
        String newToken = refreshToken(token);
        System.out.println("Refreshed Token: " + newToken);

        // 获取 Token 有效期
        long validity = getTokenValidity(token);
        System.out.println("Token Validity (ms): " + validity);
    }
}