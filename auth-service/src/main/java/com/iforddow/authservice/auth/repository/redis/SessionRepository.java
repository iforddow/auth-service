package com.iforddow.authservice.auth.repository.redis;

import com.iforddow.authservice.auth.entity.entity.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * A service class dressed as a repository for
 * session management using Redis.
 *
 * @author IFD
 * @since 2025-11-11
 * */
@Service
@RequiredArgsConstructor
public class SessionRepository {

    // Prefix for session keys in Redis
    @Value("${redis.session.prefix}")
    private String sessionPrefix;

    @Value("${redis.session.account.prefix}")
    private String accountSessionPrefix;

    // Redis template for session operations
    private final RedisTemplate<String, Session> sessionRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * A method to save a session to Redis.
     *
     * @param session The session object to be saved.
     *
     * @author IFD
     * @since 2025-11-11
     * */
    public void save(Session session) {
        String sessionKey = sessionPrefix + session.getSessionId();
        String userSessionsKey = accountSessionPrefix + session.getAccountId().toString();

        // Save session object in Redis
        sessionRedisTemplate.opsForValue().set(sessionKey, session);
        sessionRedisTemplate.expireAt(sessionKey, session.getExpiresAt());

        // Add session ID to the user's set of sessions
        stringRedisTemplate.opsForSet().add(userSessionsKey, session.getSessionId());
        stringRedisTemplate.expireAt(userSessionsKey, session.getHardExpiration());
    }

    /**
     * A method to find a session by its ID.
     *
     * @param sessionId The ID of the session to be retrieved.
     * @return An Optional containing the session if found, or empty if not found.
     *
     * @author IFD
     * @since 2025-11-11
     * */
    public Optional<Session> findById(String sessionId) {
        String key = sessionPrefix + sessionId;
        Session session = sessionRedisTemplate.opsForValue().get(key);
        return Optional.ofNullable(session);
    }

    /**
     * A method to get all sessions for a user by their account ID.
     *
     * @param accountId The ID of the account whose sessions are to be retrieved.
     * @return A list of Session objects associated with the account.
     *
     * @author IFD
     * @since 2025-11-11
     * */
    public List<Session> findAllByAccountId(UUID accountId) {
        // Retrieve all session IDs for the given account ID
        Set<String> sessionIds = stringRedisTemplate.opsForSet().members(accountSessionPrefix + accountId.toString());

        // If no session IDs found, return empty list
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        // Construct session keys and retrieve session objects
        List<String> sessionKeys = sessionIds.stream()
                .map(id -> sessionPrefix + id)
                .toList();

        // Bulk get sessions from Redis
        List<Session> sessions = sessionRedisTemplate.opsForValue().multiGet(sessionKeys);

        // If no sessions found, return empty list
        if(sessions == null || sessions.isEmpty()) {
            return List.of();
        }

        // Remove null entries in case some sessions were not found
        return sessions.stream()
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * A method to delete a session by its ID.
     *
     * @param sessionId The ID of the session to be deleted.
     *
     * @author IFD
     * @since 2025-11-11
     * */
    public void delete(String sessionId, UUID accountId) {
        String key = sessionPrefix + sessionId;
        sessionRedisTemplate.delete(key);
        stringRedisTemplate.opsForSet().remove(accountSessionPrefix + accountId);
    }

    /**
     * A method to delete a session by its object.
     *
     * @param session The Session object.
     *
     * @author IFD
     * @since 2025-11-11
     * */
    public void delete(Session session) {
        String key = sessionPrefix + session.getSessionId();
        sessionRedisTemplate.delete(key);
        stringRedisTemplate.opsForSet().remove(accountSessionPrefix + session.getAccountId());
    }

    /**
     * A method to delete multiple sessions by the user ID.
     *
     * @param userId The ID of the user whose sessions are to be deleted.
     *
     * @author IFD
     * @since 2025-11-11
     * */
    public void deleteAllByUser(UUID userId) {

        Set<String> sessionIds = stringRedisTemplate.opsForSet().members(accountSessionPrefix + userId.toString());

        if(sessionIds != null && !sessionIds.isEmpty()) {
            List<String> sessionKeys = sessionIds.stream()
                    .map(id -> sessionPrefix + id)
                    .toList();

            sessionRedisTemplate.delete(sessionKeys);
        }

        stringRedisTemplate.delete(accountSessionPrefix + userId);
    }

    /**
     * A method to extend the TTL of a session.
     *
     * @param session The session object to be extended.
     * @param ttl The duration to extend the session's TTL.
     *
     * @author IFD
     * @since 2025-11-11
     * */
    public void extendTTL(Session session, Duration ttl) {
        String key = sessionPrefix + session.getSessionId();

        Instant now = Instant.now();
        Instant newExpiry = now.plus(ttl);

        if(newExpiry.isAfter(session.getHardExpiration())) {
            newExpiry = session.getHardExpiration();
        }

        session.setExpiresAt(newExpiry);
        sessionRedisTemplate.opsForValue().set(key, session);

        sessionRedisTemplate.expireAt(key, newExpiry);
    }

}
