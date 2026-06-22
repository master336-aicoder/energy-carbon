package com.seatone.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 提供 Token 生成、解析、验证、刷新等功能
 */
@Slf4j
public class JwtUtils {

    private static final String SECRET = "seatone-energy-carbon-platform-secret-key-2026";
    private static final long ACCESS_EXPIRE = 7200000;      // 2小时
    private static final long REFRESH_EXPIRE = 604800000;   // 7天
    /**
     * Token 刷新阈值：当 Token 剩余有效期小于此值时，自动刷新
     * 默认 30 分钟
     */
    private static final long REFRESH_THRESHOLD = 1800000;  // 30分钟

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成访问 Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 访问 Token
     */
    public static String generateAccessToken(Long userId, String username) {
        return generateToken(userId, username, ACCESS_EXPIRE);
    }

    /**
     * 生成刷新 Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 刷新 Token
     */
    public static String generateRefreshToken(Long userId, String username) {
        return generateToken(userId, username, REFRESH_EXPIRE);
    }

    /**
     * 生成 Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param expire   过期时间（毫秒）
     * @return Token 字符串
     */
    private static String generateToken(Long userId, String username, long expire) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token Token 字符串
     * @return Claims 对象
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期");
            throw new RuntimeException("登录已过期，请重新登录");
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            throw new RuntimeException("无效的登录凭证");
        }
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token Token 字符串
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token Token 字符串
     * @return 用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username").toString();
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token Token 字符串
     * @return true=有效；false=无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取 Token 过期时间
     *
     * @param token Token 字符串
     * @return 过期时间
     */
    public static Date getExpirationDate(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 获取 Token 剩余有效时间（毫秒）
     *
     * @param token Token 字符串
     * @return 剩余有效时间（毫秒），已过期返回 0
     */
    public static long getRemainingTime(String token) {
        try {
            Date expiration = getExpirationDate(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(remaining, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 判断 Token 是否需要刷新
     * 当 Token 剩余有效期小于阈值时返回 true
     *
     * @param token Token 字符串
     * @return true=需要刷新；false=不需要刷新
     */
    public static boolean needRefresh(String token) {
        long remainingTime = getRemainingTime(token);
        return remainingTime > 0 && remainingTime < REFRESH_THRESHOLD;
    }

    /**
     * 刷新 Token
     * 生成新的访问 Token，保留原有用户信息
     *
     * @param token 原 Token
     * @return 新的访问 Token
     */
    public static String refreshToken(String token) {
        Claims claims = parseToken(token);
        Long userId = Long.valueOf(claims.get("userId").toString());
        String username = claims.get("username").toString();
        return generateAccessToken(userId, username);
    }

    /**
     * 生成 Token 对（访问 Token + 刷新 Token）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return Map 包含 accessToken 和 refreshToken
     */
    public static Map<String, String> generateTokenPair(Long userId, String username) {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", generateAccessToken(userId, username));
        tokenMap.put("refreshToken", generateRefreshToken(userId, username));
        return tokenMap;
    }
}
