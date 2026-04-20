package com.wjy.personal_blog.rabbitmq;

import com.wjy.personal_blog.configs.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
* 邮件消息消费者
* 从队列中获取消息，处理邮件发送业务（将验证码发送至邮件）
  * */
@Component
@Slf4j
public class EmailConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;


    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 监听邮件队列，处理邮件发送业务
     * @param emailMessage
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConfig.EMAIL_QUEUE) ,
            exchange = @Exchange(name = RabbitMQConfig.EMAIL_EXCHANGE),
            key = RabbitMQConfig.EMAIL_ROUTING_KEY
    ))
    public void emailListener(EmailMessage emailMessage){
        try {
            log.info("开始处理邮件发送任务：{}", emailMessage.getToEmail());

            // 创建邮件对象
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(emailFrom);
            msg.setTo(emailMessage.getToEmail());
            msg.setSubject(emailMessage.getSubject());
            msg.setText(emailMessage.getContent());

            // 发送邮件
            mailSender.send(msg);
            log.info("邮件成功发送至邮箱：{}", emailMessage.getToEmail());
        } catch (Exception e) {
            log.error("邮件发送失败：{}，错误信息：{}", emailMessage.getToEmail(), e.getMessage());
            //后续可添加重试机制
        }
    }
}
