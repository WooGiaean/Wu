package com.wjy.personal_blog.rabbitmq;

import com.wjy.personal_blog.configs.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* 邮件消息生产者
 * 将消息发送至队列，不直接发送至邮箱
* */
@Component
@Slf4j
public class EmailProducer {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送验证码邮件
       @params toEmail 收件人邮箱
       @params verifyCode 验证码
     *
     */
    public void sendVerifyCode2Email(String toEmail,String verifyCode){
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setToEmail(toEmail);
        emailMessage.setSubject("【个人博客】邮箱验证码");
        emailMessage.setContent("【个人博客】您的验证码为：" + verifyCode + "，有效期5分钟。");
        emailMessage.setVerifyCode(verifyCode);
        emailMessage.setEmailType("verify_code");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                emailMessage
        );
        log.info("验证码邮件发送任务已提交到邮箱队列：{}", toEmail);
    }


    /**
     * 发送通知邮件
     * @param toEmail 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendNotification2Email(String toEmail, String subject, String content) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setToEmail(toEmail);
        emailMessage.setSubject(subject);
        emailMessage.setContent(content);
        emailMessage.setEmailType("notification");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                emailMessage
        );
        log.info("通知邮件发送任务已提交到队列：{}", toEmail);
    }
}
