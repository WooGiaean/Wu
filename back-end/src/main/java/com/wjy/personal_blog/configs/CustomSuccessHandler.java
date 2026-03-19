package com.wjy.personal_blog.configs;

import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
       /* // 获取用户名
        String username = authentication.getName();
        // 查询用户信息
        User user = userMapper.findByUsername(username);
        log.info("查询用户:{}",user);
        if (user != null) {
            // 设置BaseContext
            BaseContext.setCurrentId(user.getUserId());
            // 存入session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            log.info("用户{}成功存入session",user.getUserName());
        }else{
            log.error("用户{}不存在",username);
            return;
        }*/
        //设置session信息，前端可能需要使用
        HttpSession session = request.getSession();
        session.setAttribute("user", authentication.getPrincipal());

        // 检查用户是否具有admin权限
        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("admin"));
        
        if (isAdmin) {
            // 管理员跳转到后台管理页面
            response.sendRedirect("/Admin/InfoCenter.html");
        } else {
            // 普通用户跳转到用户首页
            response.sendRedirect("/Home/home1.html");
        }
    }
}
