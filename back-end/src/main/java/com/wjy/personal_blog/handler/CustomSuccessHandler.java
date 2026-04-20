package com.wjy.personal_blog.handler;

import com.alibaba.fastjson.JSONObject;
import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.custom.UserStatisticsService;
import com.wjy.personal_blog.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.wjy.personal_blog.constants.RedisConstant.*;

@Slf4j
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserStatisticsService userStatisticsService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 获取用户名
        String username = authentication.getName();
        // 查询用户信息
        User user = userMapper.findByUsername(username);
        log.info("查询用户:{}", user.getUserName());
        userStatisticsService.setUserStatistics(user);

        //用户存在，生成JWT令牌信息
        //生成JWT令牌信息
        String token = jwtUtil.generateToken(user.getUserId(), user.getUserName(), user.getUserRole());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);


        // === 新增Redis存储逻辑 ===
        // 存储用户信息和token到Redis，value为包含用户信息和token的Map
        String userKey= CURRENT_USER_KEY+user.getUserId();
        long remainingTTL = jwtUtil.getRemainingExpirationTimeFromToken(token);
        redisTemplate.opsForValue().set(userKey,map,remainingTTL, TimeUnit.MILLISECONDS);

       /* // 存储用户信息到Redis
        String userInfoKey = USER_INFO_PREFIX + user.getUserId();
        redisTemplate.opsForValue().set(userInfoKey, user, remainingTTL, TimeUnit.MILLISECONDS);*/

        // 存储token到用户id的映射: key为token，value为用户ID(映射关系)
        String tokenKey = USER_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, user.getUserId(), remainingTTL, TimeUnit.MILLISECONDS);
        // === Redis存储逻辑结束 ===
        log.info("Redis存储逻辑结束");

        //使用自定义Result封装返回信息
        Result<Map<String, Object>> result = Result.success(map);
        String json = JSONObject.toJSONString(result);
        //将JSON字符串写入响应体
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);

    }
}
