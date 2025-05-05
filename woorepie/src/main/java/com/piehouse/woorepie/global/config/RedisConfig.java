package com.piehouse.woorepie.global.config;

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
    public RedisTemplate<String, RedisEstateTradeValue> redisEstateTradeTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisEstateTradeValue> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // RedisEstateTradeValue 직렬화

        return template;
    }

    @Bean
    public RedisTemplate<String, RedisCustomerTradeValue> redisCustomerTradeTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisCustomerTradeValue> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // RedisCustomerTradeValue 직렬화

        return template;
    }
}