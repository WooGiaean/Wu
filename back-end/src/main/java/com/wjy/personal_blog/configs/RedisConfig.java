package com.wjy.personal_blog.configs;

import com.wjy.personal_blog.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/*
* 注册redis连接源
*  */
@Configuration
@Slf4j
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建Redis模板对象");
        //创建Redis对象
        RedisTemplate redis=new RedisTemplate();
        //设置redis连接工厂对象
       redis.setConnectionFactory(redisConnectionFactory);
        /*RedisConnection connection = redisConnectionFactory.getConnection();
        connection.select(dateBase);
        connection.close();*/
        //引入时间的序列化器
        JacksonObjectMapper objectMapper = new JacksonObjectMapper();

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);


        //设置redis的key序列化器
        redis.setKeySerializer(new StringRedisSerializer());
        redis.setHashKeySerializer(new StringRedisSerializer());
        //设置redis的value序列化器
        redis.setValueSerializer(serializer);
        redis.setHashValueSerializer(serializer);
        // 初始化RedisTemplate
        redis.afterPropertiesSet();
        //成功返回redis对象
        return redis;
    }

}
