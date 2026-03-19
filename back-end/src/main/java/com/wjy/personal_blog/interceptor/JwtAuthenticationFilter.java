package com.wjy.personal_blog.interceptor;

import com.wjy.personal_blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws ServletException, IOException {
        
        // 从请求头中获取令牌
        String token = request.getHeader("Authorization");
        
        // 检查令牌是否存在且格式正确
        if (token != null && token.startsWith("Bearer ")) {
            try {
                // 提取令牌
                token = token.substring(7); // 去除 "Bearer " 前缀
                
                // 验证令牌
                if (JwtUtil.validateToken(token)) {
                    // 解析令牌，获取用户信息
                    Claims claims = JwtUtil.parseToken(token);
                    Long userId = JwtUtil.getUserIdFromToken(token);
                    String username = JwtUtil.getUsernameFromToken(token);
                    String role = JwtUtil.getRoleFromToken(token);
                    
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, 
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                    );
                    
                    // 设置认证对象到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 令牌无效，清除安全上下文
                SecurityContextHolder.clearContext();
            }
        }
        
        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
}
