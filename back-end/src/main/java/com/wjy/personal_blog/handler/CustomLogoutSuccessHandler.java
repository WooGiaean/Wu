package com.wjy.personal_blog.handler;

import com.alibaba.fastjson.JSONObject;
import com.wjy.personal_blog.exceptions.BusinessException;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.ByteChunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.wjy.personal_blog.constants.RedisConstant.*;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(6).trim();
            log.info("用户退出登录，令牌: {}", token);
            try {
                // 获取用户ID
                Integer userId = jwtUtil.getUserIdFromToken(token);
                
                /*// 清理用户信息
                String userInfoKey = USER_INFO_PREFIX + userId;
                redisTemplate.delete(userInfoKey);
                */
                // 清理token映射
                String tokenKey = USER_TOKEN_PREFIX + token;
                redisTemplate.delete(tokenKey);
                
                // 清理当前用户信息和token的Map
                String currentUserKey = CURRENT_USER_KEY + userId;
                redisTemplate.delete(currentUserKey);
                
                // 退出登录后，将token加入黑名单
                long remainingTime = jwtUtil.getRemainingExpirationTimeFromToken(token);
                if (remainingTime > 0) {
                    String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
                    redisTemplate.opsForValue().set(blacklistKey, "1", remainingTime, TimeUnit.MILLISECONDS);
                }
                
                log.info("用户 {} 退出登录，已清理Redis中的用户信息和token", userId);
                //log.info("用户退出登录，已清理Redis中的用户信息和token");
            } catch (Exception e) {
                log.error("退出登录时清理Redis失败", e);
                throw new BusinessException("退出登录时清理Redis失败:" + e.getMessage());
                // 即使清理失败，也要返回成功响应
            }

        }
        // 返回退出成功响应
        Result<String> result = Result.success("退出成功");
        String json = JSONObject.toJSONString(result);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        
        /*// 返回退出成功响应
        Result<String> result = Result.success("退出成功");
        String json = JSONObject.toJSONString(result);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);*/
    }
}