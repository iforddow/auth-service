// Java
package com.iforddow.authservice.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iforddow.authservice.auth.entity.entity.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
* A configuration class for Redis and Redis templates.
*
* @author IFD
* @since 2025-11-17
* */
@Configuration
public class RedisConfig {

    /**
    * A RedisTemplate bean for general Object storage.
    *
    * @param factory The Redis connection factory.
    *
    * @return RedisTemplate<String, Object>
    *
    * @author IFD
    * @since 2025-11-17
    * */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }

    /**
     * A RedisTemplate bean specifically for Session objects.
     *
     * @param factory The Redis connection factory.
     * @param objectMapper The ObjectMapper for serialization.
     *
     * @return RedisTemplate<String, Session>
     * @author IFD
     * */
    @Bean
    public RedisTemplate<String, Session> sessionRedisTemplate(RedisConnectionFactory factory,
                                                               ObjectMapper objectMapper) {
        return buildTypedTemplate(factory, objectMapper, Session.class);
    }

    /**
     * A method to build a typed RedisTemplate.
     *
     * @param factory The Redis connection factory.
     * @param objectMapper The ObjectMapper for serialization.
     * @param clazz The class type for the template.
     *
     * @author IFD
     * @since 2025-11-17
     * */
    private <T> RedisTemplate<String, T> buildTypedTemplate(
            RedisConnectionFactory factory,
            ObjectMapper objectMapper,
            Class<T> clazz
    ) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<T> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, clazz);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }



}
