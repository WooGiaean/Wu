package com.wjy.personal_blog.configs;

import com.wjy.personal_blog.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件队列配置
 */
@Configuration
@Slf4j
public class RabbitMQConfig {


    @Bean
    public MessageConverter jacksonMessageConverter() {
        //Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        return new Jackson2JsonMessageConverter(new JacksonObjectMapper());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter());
        return template;
    }



    public static final String EMAIL_EXCHANGE = "email.direct";
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String EMAIL_ROUTING_KEY = "email.code";

    /**
     * 创建邮件队列
     */
    @Bean
    public Queue emailQueue() {
        //持久化队列
        return new Queue(EMAIL_QUEUE, true);
    }


    /**
     * 创建交换机（DirectExchange）
     */
    @Bean
    public DirectExchange emailExchange() {
        //持久化交换机,暂时关闭自动删除
        return new DirectExchange(EMAIL_EXCHANGE, true, false);
    }

    /**
     * 绑定交换机和队列
     */
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())//绑定队列
                .to(emailExchange())//绑定交换机
                .with(EMAIL_ROUTING_KEY); //绑定routing_key参数
    }
}
