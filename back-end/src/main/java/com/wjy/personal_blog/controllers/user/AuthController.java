package com.wjy.personal_blog.controllers.user;

import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.UserService;
import com.wjy.personal_blog.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录结果，包含JWT令牌
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            // 进行身份认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );
            
            // 认证成功，获取用户信息
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            // 生成JWT令牌
            String token = JwtUtil.generateToken(
                    user.getUserId().longValue(),
                    user.getUserName(),
                    user.getUserRole()
            );
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            
            return Result.success(response);
        } catch (AuthenticationException e) {
            log.error("登录失败：{}", e.getMessage());
            return Result.error("用户名或密码错误");
        }
    }

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            userService.insertNewUser(user);
            return Result.success();
        } catch (RuntimeException e) {
            log.error("注册失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
