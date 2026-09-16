package com.wjy.personal_blog.rabbitmq;

import com.wjy.personal_blog.configs.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 死信消息消费者
 * 处理邮件发送失败的消息，可进行人工干预或记录
 */
@Component
@Slf4j
public class DeadConsumer {
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConfig.DEAD_QUEUE) ,
            exchange = @Exchange(name = RabbitMQConfig.DEAD_EXCHANGE),
            key = RabbitMQConfig.DEAD_ROUTING_KEY))
    public void handleDeadLetter(Message message) {
        log.warn("========== 收到死信消息 ==========");
        log.warn("消息ID: {}", message.getMessageProperties().getMessageId());
        log.warn("消息内容: {}", new String(message.getBody()));
        log.warn("死信原因: {}", message.getMessageProperties().getXDeathHeader());
        // 可选：保存到数据库或发送告警通知
        // 1. 保存到数据库进行人工处理
        // 2. 发送短信或备用邮箱通知管理员
        // 3. 记录日志供后续分析

        log.error("邮件发送失败消息已记录，等待人工处理");
    }
}
