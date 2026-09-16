package com.wjy.personal_blog.rabbitmq;

import com.wjy.personal_blog.configs.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

        /*rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                emailMessage
        );*/
        sendWithConfirm(emailMessage);

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

    /**
     * 发送消息（带 Publisher Confirm 确认机制）
     */
    private void sendWithConfirm(EmailMessage emailMessage) {
        // 创建关联数据，用于跟踪消息确认
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        // 发送消息
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                emailMessage,
                correlationData
        );

        // 等待确认结果
        try {
            CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);
            if (confirm != null && confirm.isAck()) {
                log.info("消息已确认到达 MQ，ID: {}", correlationData.getId());
            } else {
                log.error("消息未确认到达 MQ，ID: {}，原因: {}",
                        correlationData.getId(),
                        confirm != null ? confirm.getReason() : "未知");
                // 重试3次
                retrySend(emailMessage, 3);
            }
        } catch (Exception e) {
            log.error("等待消息确认超时或异常: {}", e.getMessage());
            retrySend(emailMessage, 3);
        }
    }


    /**
     * 重试发送消息
     */
    private void retrySend(EmailMessage emailMessage, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            retryCount++;
            try {
                log.warn("第 {} 次重试发送消息", retryCount);
                CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EMAIL_EXCHANGE,
                        RabbitMQConfig.EMAIL_ROUTING_KEY,
                        emailMessage,
                        correlationData
                );

                CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);
                if (confirm != null && confirm.isAck()) {
                    log.info("重试成功，消息已确认到达 MQ");
                    return;
                }
            } catch (Exception e) {
                log.error("重试失败: {}", e.getMessage());
            }
        }
        log.error("消息发送失败，已达到最大重试次数");
    }


}
