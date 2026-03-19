/*
package com.wjy.personal_blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // 密钥，实际项目中应该配置在配置文件中
    private static final String SECRET_KEY = "your-secret-key"; 
    
    // 令牌过期时间，单位：毫秒（这里设置为24小时）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    */
/**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT令牌
     *//*

    public static String generateToken(Long userId, String username, String role) {
        // 创建令牌声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        // 生成令牌
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    */
/**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return 令牌声明
     *//*

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    */
/**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     *//*

    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    */
/**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     *//*

    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    */
/**
     * 从令牌中获取用户角色
     * @param token JWT令牌
     * @return 用户角色
     *//*

    public static String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    */
/**
     * 验证令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     *//*

    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // 检查令牌是否过期
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // 令牌解析失败，说明令牌无效
            return false;
        }
    }
}
*/
