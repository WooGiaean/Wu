package com.wjy.personal_blog.service.impl;



import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.VerifyCodeDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.utils.GenerateVerifyCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EmailService {

    //邮件验证码发出地
    @Value("${spring.mail.username}")
    private String emailFrom;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public EmailService(JavaMailSender javaMailSender){
        this.mailSender =javaMailSender;
    }

    /*
    * 发送验证码到邮箱
    * */
    public void sendEmailVerifyCode(String toEmail){
        //生成6位数字验证码
        String code = GenerateVerifyCode.generateCode6();
        //将对应邮箱和验证码存到redis缓存，方便登录时验证获取
        redisTemplate.opsForValue().set(RedisConstant.VERIFY_CODE_KEY+toEmail,code,3*60, TimeUnit.SECONDS);

        //创建邮件对象，并讲验证码发到邮件
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setFrom(emailFrom);
        msg.setTo(toEmail);
        msg.setSubject("【个人博客】邮箱验证码");
        msg.setText("【个人博客】您的验证码为："+code);
        mailSender.send(msg);

    }

    /*
    * 邮箱验证码登录
    * */
    public User loginWithVerifyCode(VerifyCodeDTO verifyCodeDTO){
        //从redis获取验证码
        String veryCode = (String)redisTemplate.opsForValue()
                .get(RedisConstant.VERIFY_CODE_KEY + verifyCodeDTO.getEmail());

        if(veryCode!=null && veryCode.equals(verifyCodeDTO.getCode())){
            //验证码正确，登录成功
            log.info("用户登录成功：{}",verifyCodeDTO.getEmail());
            User userByEmail = userMapper.getUserByEmail(verifyCodeDTO.getEmail());
            return userByEmail;
        }
        return null;
    }


}
