package com.wjy.personal_blog.interceptor;

import com.wjy.personal_blog.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Component
@Slf4j
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User)request.getSession().getAttribute("user");
        if (user == null) {
            //log.info("用户信息:"+user.toString());
            log.warn("用户未登录,跳转到登录页面:"+null);
            //重写跳转到登录页面
            //response.sendRedirect("/Admin/login.html");
            return false;
        }
        //以非管理员身份登录
        log.info(user.getUserRole());
        if (!Objects.equals(user.getUserRole(), User.USER_ROLE_ADMIN)) {
            log.warn("{} 非管理员admin",user.getUserNickname());
            //response.sendRedirect("/error/403.html");
            return false;
        }

        // 登录成功，以管理员进行登录
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
