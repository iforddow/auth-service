package com.iforddow.authservice.common.utility;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
* A component class for checking maximum limits.
*
* @author IFD
* @since 2025-12-05
* */
@Component
@RequiredArgsConstructor
public class CheckMax {

    private final StringRedisTemplate stringRedisTemplate;

    /**
    * A method to check if the maximum number of attempts has been reached for a given key.
    *  This method also increments the attempt count if the maximum has not been reached,
    *  and sets the TTL for the key if it is not already set.
    *
    * @param key The key to check the attempts for.
    * @param max The maximum number of allowed attempts. Use -1 for unlimited attempts.
    * @param ttlSeconds The time-to-live in seconds for the key.
    *
    * @author IFD
    * @since 2025-12-05
    * */
    public boolean maxReached(String key, int max, int ttlSeconds) {

        //Check to make sure max attempts not exceeded
        String attemptsValue = stringRedisTemplate.opsForValue().get(key);
        int attempts = attemptsValue != null ? Integer.parseInt(attemptsValue) : 0;

        if (max != -1) {
            if(attempts >= max) {
                return true;
            }   else {
                increaseAttempts(key, ttlSeconds);
            }
        }

        return false;

    }

    /**
    * A method to increase the number of attempts for a given key.
    *  This method also sets the TTL for the key if it is not already set.
    *
    * @param key The key to increase the attempts for.
    * @param ttlSeconds The time-to-live in seconds for the key.
    *
    * @author IFD
    * @since 2025-12-05
    * */
    public void increaseAttempts(String key, int ttlSeconds) {

        Long attempts = stringRedisTemplate.opsForValue().increment(key);

        // Only set TTL on first increment
        if (attempts != null && attempts == 1) {
            stringRedisTemplate.expireAt(key, Instant.now().plus(Duration.ofSeconds(ttlSeconds)));
        }
    }



    /**
    * A method to get the current number of attempts for a given key.
    *
    * @param key The key to get the attempts for.
    * @return The current number of attempts.
    *
    * @author IFD
    * @since 2025-12-05
    * */
    public int getAttempts(String key) {
        String attemptsValue = stringRedisTemplate.opsForValue().get(key);
        return attemptsValue != null ? Integer.parseInt(attemptsValue) : 0;
    }

}
