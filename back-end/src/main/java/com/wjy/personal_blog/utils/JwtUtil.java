package com.wjy.personal_blog.utils;


import com.wjy.personal_blog.exceptions.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {


    //密钥
    @Value("${jwt.secret}")
    private  String secretKey;

    // 生成安全的密钥
    //private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private SecretKey getSecretKey() {
        // 确保密钥长度足够（至少32字节）
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        // 使用Keys.hmacShaKeyFor生成符合HS256算法要求的密钥
        return Keys.hmacShaKeyFor(keyBytes);
    }


    //定义jwt的有效期
    @Value("${jwt.expiration}")
    private long expirationTime;

    // 生成随机密钥的工具方法
    /*public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }*/


    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT令牌
     */
    public String generateToken(int userId, String username, String role) {
        // 创建令牌声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        // 生成令牌
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setIssuedAt(new Date())
                .signWith(getSecretKey())
                .compact();
    }

   /*
   * 解析JWT令牌
   * */
    /**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return 令牌声明
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()  // 使用parserBuilder
                .setSigningKey(getSecretKey())  // 使用SecretKey对象
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public int getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Integer.class);
    }

    /**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从令牌中获取用户角色
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }


    /**
     * 获取令牌有效期
     * 计算令牌剩余有效期（毫秒）
     * @param token JWT令牌
     * @return 令牌剩余有效期（毫秒）
     * 如果令牌已过期，则返回0
     */
    public long getRemainingExpirationTimeFromToken(String token) {
        Date expiration = parseToken(token).getExpiration();
        long currentTime = System.currentTimeMillis();
        return Math.max(0, expiration.getTime() - currentTime);
    }

    /**
     * 验证令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()  // 使用parserBuilder
                    .setSigningKey(getSecretKey())  // 使用SecretKey对象
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("令牌验证失败: {}", e.getMessage());
            return false;
        }
    }
}
