package com.piehouse.woorepie.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piehouse.woorepie.trade.dto.request.RedisCustomerTradeValue;
import com.piehouse.woorepie.trade.dto.request.RedisEstateTradeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        // @class 필드 없이 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisTemplate<String, String> redisStringTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer()); // Lua 결과를 String으로 받기 위함
        return template;
    }

    @Bean
    public RedisTemplate<String, RedisEstateTradeValue> redisEstateTradeTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisEstateTradeValue> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        // @class 필드 없이 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }

    @Bean
    public RedisTemplate<String, RedisCustomerTradeValue> redisCustomerTradeTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisCustomerTradeValue> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        // @class 필드 없이 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }
}

