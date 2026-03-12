package com.wjy.personal_blog.controllers.admin;

import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.dto.VerifyCodeDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.UserService;
import com.wjy.personal_blog.service.impl.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/admin")
@Slf4j
@Tag(name = "后台接口",description = "后台管理端接口")
public class AdminController {

    @Autowired
    private UserService userService;


    @Autowired
    private EmailService emailService;


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
    * 默认访问路径是登录界面
    * */
    @GetMapping("/")
    public String loginPage(){
        return "redirect:/Login/login.html";
    }


    /**
     * 登录验证
     */

    @PostMapping("/loginCheck")
    @ResponseBody
    @Operation(summary = "登录验证",description = "用户登录验证接口")
    public Result loginCheck(@RequestBody LoginDTO loginDTO,HttpSession session){
        log.info("用户登录验证:{}",loginDTO.getUsername()+":"+loginDTO.getPassword());
        if(loginDTO==null){
            return Result.error("用户名或密码为空");
        }
        
        // 根据用户名查询用户
        User user = userService.findByUsername(loginDTO.getUsername());
        if(user == null){
            return Result.error("用户名或密码错误");
        }
        
        // 验证密码
        boolean passwordMatch = false;
        try {
            // 检查密码是否已经加密
            if(user.getUserPassword().startsWith("$2a$")) {
                // 使用BCryptPasswordEncoder验证加密密码
                passwordMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getUserPassword());
            } else {
                // 暂时支持明文密码（用于兼容旧数据）
                passwordMatch = loginDTO.getPassword().equals(user.getUserPassword());
            }
        } catch (Exception e) {
            log.error("密码验证失败", e);
            return Result.error("登录失败");
        }

        if(!passwordMatch){
            return Result.error("用户名或密码错误");
        }



        log.info("登录用户：{}",user);
        //存入用户session的id，作为唯一标识
        session.setAttribute("user",user);
        //将当前登录用户的id存到ThreadLocal中
        BaseContext.setCurrentId(user.getUserId());
        log.info("当前用户的JSESSIONID：{}",user.getUserId());
        return Result.success(user);
    }


    /*
    * 特殊账户
    * */
    @RequestMapping("/tempLogin")
    public String tempLogin(){
        return "redirect:/Home/pic.html";
    }


    /**
     * 注册验证
     */

    @PostMapping("/register")
    @ResponseBody
    @Operation(summary = "用户注册验证",description = "用户注册验证接口")
    public Result register(@RequestBody UserDTO userDTO){
        log.info("用户注册验证：{}",userDTO);
        //查询前端传递的用户数据是否已经存在数据库中
        User user=new User();
        BeanUtils.copyProperties(userDTO,user);
        //加密密码
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        userService.insertNewUser(user);
        log.info("用户注册成功：{}",user);
        return Result.success(userDTO);
    }


    /**
    * 用户简介页面显示
    * */

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @ResponseBody
    @Operation(summary = "用户退出登录",description = "用户退出登录接口")
    public Result<String> logoutPage(HttpSession session){
        log.info("用户退出登录");
        session.invalidate();   //让session失效
        BaseContext.removeCurrentId();
        return Result.success("成功退出登录");
    }
    
    
    /**
     * 邮件发送验证码
     * */
    @PostMapping("/code2Email")
    @ResponseBody
    @Operation(summary = "发送邮箱验证码",description = "用户发送邮箱验证码接口")
    public Result sendEmailCode(@RequestBody VerifyCodeDTO codeDTO){
        String email = codeDTO.getEmail();
        //判断邮箱是否为空
        if(email==null){
            return Result.error("邮箱不能为空");
        }
        //判断邮箱格式：使用正则表达式
        if (!email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            return Result.error("邮箱格式不正确");
        }

        //防止重复发送验证码
        String redisEmail = RedisConstant.EMAIL_SEND_RATE_LIMIT + email;
        if(redisTemplate.hasKey(redisEmail)){
            return Result.error("请勿重复发送验证码");
        }
        //设置验证码发送频率
        redisTemplate.opsForValue().set(redisEmail,1,60, TimeUnit.SECONDS);

        log.info("发送验证码到邮箱：{}",email);
        emailService.sendEmailVerifyCode(email);
        log.info("验证码发送成功");
        return Result.success("验证码发送成功");
    }


    /**
    * 验证码登录
    * */
    @PostMapping("/verifyCodeLogin")
    @ResponseBody
    @Operation(summary = "邮箱验证码登录",description = "用户邮箱验证码登录接口")
    public Result loginWithVerifyCode(@RequestBody VerifyCodeDTO verifyDTO,HttpSession session){
        log.info("用户验证码登录：{}",verifyDTO.getEmail());
        User user = emailService.loginWithVerifyCode(verifyDTO);
        if(user==null){
            return Result.error("当前邮箱用户不存在");
        }
        log.info("用户验证码登录成功：{}",user);
        session.setAttribute("user",user);
        BaseContext.setCurrentId(user.getUserId());
        return Result.success(user);
    }
}
