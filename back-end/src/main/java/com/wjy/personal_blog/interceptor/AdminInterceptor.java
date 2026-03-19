package com.wjy.personal_blog.interceptor;

import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/*
* 管理员相关拦截器
* */

@Component
@Slf4j
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User)request.getSession().getAttribute("user");
        //判断用户是否为空
        if (user == null) {
            //log.info("用户信息:"+user.toString());
            log.warn("用户未登录");
            response.sendRedirect("/Login/login.html");
            return false;
        }

        log.info("用户角色："+user.getUserRole());

        //非管理员身份
        if(!user.getUserRole().equals("admin")){
            log.warn("用户非管理员身份，请勿访问管理员页面");
            return false;
        }

        // 登录成功，以正常用户进行登录
        //将当前登录用户的id存放到线程中，方便后续获取。不用每次都调用session获取
        Integer userId = user.getUserId();
        BaseContext.setCurrentId(userId);
        log.info("管理员已登录，id为：{}",userId);
        //进行放行
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentId();
    }
}
