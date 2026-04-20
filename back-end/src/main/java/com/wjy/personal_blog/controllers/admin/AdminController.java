package com.wjy.personal_blog.controllers.admin;

import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.dto.VerifyCodeDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.UserService;
import com.wjy.personal_blog.service.email.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/front/admin")
@Slf4j
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
     * 登录验证
     */

   /* @PostMapping("/loginCheck")

    (summary = "登录验证",description = "用户登录验证接口")
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



        log.info("登录用户：{}",user.getUserName());
        //存入用户session的id，作为唯一标识
        session.setAttribute("user",user);
        //将当前登录用户的id存到ThreadLocal中
        BaseContext.setCurrentId(user.getUserId());
        log.info("当前用户id{}",user.getUserId());
        return Result.success(user);
    }
*/

    /**
     * 注册验证
     */

    @PostMapping("/register")

    public Result register(@RequestBody UserDTO userDTO){
        log.info("用户注册验证：{}",userDTO);
        
        // 验证码注册（从Redis中获取验证码并验证）
        if (userDTO.getCaptchaCode() != null && !userDTO.getCaptchaCode().isEmpty()) {
            // TODO: 实现验证码验证逻辑，从Redis中获取验证码并验证
            // 从redis获取验证码进行校验
            String storedCode = (String) redisTemplate.opsForValue()
                    .get(RedisConstant.VERIFY_CODE_KEY+userDTO.getUserEmail());
            // 验证码不存在或验证码错误，直接返回错误信息
            if (storedCode == null || !storedCode.equals(userDTO.getCaptchaCode())) {
                return Result.error("验证码错误或已过期");
            }
            //验证码验证成功，删除验证码
            redisTemplate.delete(RedisConstant.VERIFY_CODE_KEY+userDTO.getUserEmail());
        }

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
     * 退出登录
     */
    @PostMapping("/logout")

    public Result<String> logoutPage(){
        log.info("用户退出登录");
       /* session.invalidate();   //让session失效
        BaseContext.removeCurrentId();*/

        return Result.success("成功退出登录");
    }
    
    
    /**
     * 邮件发送验证码
     * */
    @PostMapping("/code2Email")

    public Result sendEmailCode(@RequestBody VerifyCodeDTO codeDTO){
        String email = codeDTO.getEmail();
        //限制发送频率已经再EmailService实现
       /* //防止重复发送验证码
        String redisEmail = RedisConstant.EMAIL_SEND_RATE_LIMIT + email;
        if(redisTemplate.hasKey(redisEmail)){
            return Result.error("请勿重复发送验证码");
        }*/
        try {
            emailService.sendEmailVerifyCode(email);
            log.info("验证码发送成功：{}",email);
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            log.error("验证码发送失败",e.getMessage());
            throw new RuntimeException(e);
            //return Result.error("验证码发送失败，请稍后再试。");
        }
    }


    /**
    * 验证码登录
    * */
    @PostMapping("/verifyCodeLogin")

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
