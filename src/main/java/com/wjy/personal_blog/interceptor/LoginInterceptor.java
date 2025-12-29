package com.wjy.personal_blog.interceptor;

import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User)request.getSession().getAttribute("user");
        log.info("用户信息：{}",user);
        //用户已经登录
        if(user!=null){
            BaseContext.setCurrentId(user.getUserId());
            log.debug("拦截器LoginInterceptor：设置当前用户id{}",user.getUserId());
            return true;
        }else{
            log.warn("用户未登录,进行非法访问！");
            //判断是否为ajax请求
            String accept = request.getHeader("Accept");
            log.info(accept);
           // response.sendRedirect("/Admin/login.html");

            if(accept!=null&&accept.contains("application/json")){
                //是ajax请求
                response.setStatus(401);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\",\"data\":null}");
            }else{
                //重定向到登录页面
                response.sendRedirect("/Login/login.html");
            }
        }

        return false;
    }
}
