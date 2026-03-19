package com.wjy.personal_blog.utils;

import com.wjy.personal_blog.service.impl.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/*
* 该方法用户验证用户
* */
public class SecurityUtil {

    /**
     * 获取当前登录用户的ID
     */
    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 这里需要根据你的UserDetails实现来获取用户ID
            // 假设你的UserDetails实现类是CustomUserDetails
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
            }
        }
        return null;
    }

    /**
    * 获取当前用户的所有信息
    * */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                return (CustomUserDetails) authentication.getPrincipal();
            }
        }
        return null;
    }
}
