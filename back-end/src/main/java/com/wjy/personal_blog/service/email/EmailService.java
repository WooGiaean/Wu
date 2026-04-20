package com.wjy.personal_blog.service.email;



import com.wjy.personal_blog.configs.RabbitMQConfig;
import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.exceptions.BusinessException;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.VerifyCodeDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.rabbitmq.EmailProducer;
import com.wjy.personal_blog.utils.GenerateVerifyCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EmailService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailProducer emailProducer;


/*

    //邮件验证码发出地
    //@Value("${spring.mail.username}")
    private final String emailFrom;



    private final UserMapper userMapper;


    private final JavaMailSender mailSender;
*/

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /*
    * 发送验证码到邮箱(异步操作)
    * */
    public void sendEmailVerifyCode(String toEmail){
        log.info("发送验证码到邮箱：{}",toEmail);
        //先检查发送频率，1分钟内只能发送1次
        String rateLimitKey = RedisConstant.EMAIL_SEND_RATE_LIMIT + toEmail;
        if(redisTemplate.hasKey(rateLimitKey)){
            throw new BusinessException("验证码发送频率过高,1分钟后再尝试发送");
        }

        //生成6位数字验证码
        String code = GenerateVerifyCode.generateCode6();
        //将对应邮箱和验证码存到redis缓存，方便登录时验证获取
        redisTemplate.opsForValue().set(RedisConstant.VERIFY_CODE_KEY+toEmail,
                code,
                RedisConstant.CACHE_EXPIRED_SECONDS,
                TimeUnit.SECONDS);



        // 设置验证码发送频率限制：每1分钟最多发送1次
        redisTemplate.opsForValue().set(rateLimitKey, 1, 60, TimeUnit.SECONDS);

        //异步发送邮件服务
        emailProducer.sendVerifyCode2Email(toEmail,code);

    }

    /*
    * 邮箱验证码登录
    * */
    public User loginWithVerifyCode(VerifyCodeDTO verifyCodeDTO){
        //从redis获取验证码
        String storeCode = (String)redisTemplate.opsForValue()
                .get(RedisConstant.VERIFY_CODE_KEY + verifyCodeDTO.getEmail());

        if(storeCode==null){
            throw new BusinessException("验证码已过期");
        }

        if(storeCode.equals(verifyCodeDTO.getCode())){
            //验证码正确，登录成功。
            //删除验证码
            redisTemplate.delete(RedisConstant.VERIFY_CODE_KEY+verifyCodeDTO.getEmail());
            //登录成功,返回用户信息
            User userByEmail = userMapper.getUserByEmail(verifyCodeDTO.getEmail());
            if(userByEmail==null){
                throw new BusinessException("用户不存在");
            }
            log.info("用户登录成功：{}",userByEmail);
            return userByEmail;
        }else{
            throw new BusinessException("验证码错误");
        }
    }

}
