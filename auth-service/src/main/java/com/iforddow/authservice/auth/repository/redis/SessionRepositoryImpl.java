package com.iforddow.authservice.auth.repository.redis;

import com.iforddow.authsession.common.AuthProperties;
import com.iforddow.authsession.entity.Session;
import com.iforddow.authsession.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
* A service class used as a repository for Redis-based session management.
* This is an implementation of the SessionRepository interface. Provided
* via the Session dependency package.
*
* @author IFD
* @since 2025-11-30
* */
@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {

    private final AuthProperties authProperties;
    private final RedisTemplate<String,Session> sessionRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    /**
    * A method to find a session by its ID.
    *
    * @param sessionId The ID of the session to be retrieved.
    * @return The session object if found, otherwise null.
    *
    * @author IFD
    * @since 2025-11-30
    * */
    @Override
    public Session findById(String sessionId) {
        String key = authProperties.getSessionPrefix() + sessionId;
        return sessionRedisTemplate.opsForValue().get(key);
    }

    /**
    * A method to save a session to Redis.
    *
    * @param session The session object to be saved.
    *
    * @author IFD
    * @since 2025-11-30
    * */
    @Override
    public void save(Session session) {
        String sessionKey = authProperties.getSessionPrefix() + session.getSessionId();
        String userSessionsKey = authProperties.getAccountSessionPrefix() + session.getAccountId().toString();

        // Save session object in Redis
        sessionRedisTemplate.opsForValue().set(sessionKey, session);
        sessionRedisTemplate.expireAt(sessionKey, session.getExpiresAt());

        // Add session ID to the user's set of sessions
        stringRedisTemplate.opsForSet().add(userSessionsKey, session.getSessionId());
        stringRedisTemplate.expireAt(userSessionsKey, session.getHardExpiration());
    }

    /**
    * A method to delete a session by its ID.
    *
    * @param sessionId The ID of the session to be deleted.
    *
    * @author IFD
    * @since 2025-11-30
    * */
    @Override
    public void delete(String sessionId) {
        String key = authProperties.getSessionPrefix() + sessionId;
        sessionRedisTemplate.delete(key);
        stringRedisTemplate.opsForSet().remove(authProperties.getAccountSessionPrefix() + sessionId);
    }

    /**
    * A method to delete a session by its object.
    *
    * @param session The session object to be deleted.
    *
    * @author IFD
    * @since 2025-11-30
    * */
    @Override
    public void delete(Session session) {
        String sessionId = session.getSessionId();
        delete(sessionId);
    }

    /**
    * A method to check if a session exists by its ID.
    *
    * @param sessionId The ID of the session to be checked.
    * @return true if the session exists, false otherwise.
    *
    * @author IFD
    * @since 2025-11-30
    * */
    @Override
    public boolean exists(String sessionId) {
        return sessionRedisTemplate.hasKey(authProperties.getSessionPrefix() + sessionId);
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
        Set<String> sessionIds = stringRedisTemplate.opsForSet().members(authProperties.getAccountSessionPrefix() + accountId.toString());

        // If no session IDs found, return empty list
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        // Construct session keys and retrieve session objects
        List<String> sessionKeys = sessionIds.stream()
                .map(id -> authProperties.getSessionPrefix() + id)
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

}
