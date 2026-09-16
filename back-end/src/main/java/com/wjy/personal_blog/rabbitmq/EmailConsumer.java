package com.wjy.personal_blog.rabbitmq;

import com.rabbitmq.client.Channel;
import com.wjy.personal_blog.configs.RabbitMQConfig;
import com.wjy.personal_blog.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
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

import java.io.IOException;

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
    ),ackMode = "MANUAL") //设置手动确认
    public void emailListener(EmailMessage emailMessage, Channel channel, Message message){
        // 获取消息的唯一标识
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
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
            //手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("邮件发送失败：{}，错误信息：{}", emailMessage.getToEmail(), e.getMessage());
            //后续可添加重试机制
            try {
                channel.basicNack(deliveryTag, false, false);
                log.warn("消息已拒绝并进入死信队列: {}", deliveryTag);
            } catch (IOException ex) {
                log.error("消息拒绝失败: {}", ex.getMessage());
                throw new BusinessException(ex);
            }
        }
    }
}
