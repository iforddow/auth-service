// Java
package com.iforddow.authservice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/**
* A configuration class for Redis and Redis templates.
*
* @author IFD
* @since 2025-11-17
* */
@Configuration
public class RedisConfig {


    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJacksonJsonRedisSerializer(objectMapper)))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

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
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        JacksonJsonRedisSerializer<Object> serializer =
                new JacksonJsonRedisSerializer<>(objectMapper, Object.class);

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
//    @Bean
//    public RedisTemplate<String, Session> sessionRedisTemplate(RedisConnectionFactory factory,
//                                                               ObjectMapper objectMapper) {
//        return buildTypedTemplate(factory, objectMapper, Session.class);
//    }

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

        JacksonJsonRedisSerializer<T> serializer =
                new JacksonJsonRedisSerializer<>(objectMapper, clazz);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }



}
