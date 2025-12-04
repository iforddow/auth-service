package com.iforddow.authservice.auth.repository.redis;

import com.iforddow.authservice.common.exception.ResourceNotFoundException;
import com.iforddow.authservice.common.utility.HashUtility;
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
    private final HashUtility hashUtility;

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

        String hashedSessionId = hashUtility.hmacSha256(sessionId);

        String key = authProperties.getSessionPrefix() + hashedSessionId;
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

        Session newSession = new Session(
                hashUtility.hmacSha256(session.getSessionId()),
                session.getAccountId(),
                session.getCreatedAt(),
                session.getIp(),
                session.getUserAgent(),
                session.getExpiresAt(),
                session.getHardExpiration()
        );

        String sessionKey = authProperties.getSessionPrefix() + newSession.getSessionId();
        String userSessionsKey = authProperties.getAccountSessionPrefix() + newSession.getAccountId().toString();

        // Save session object in Redis
        sessionRedisTemplate.opsForValue().set(sessionKey, newSession);
        sessionRedisTemplate.expireAt(sessionKey, newSession.getHardExpiration());

        // Add session ID to the user's set of sessions
        stringRedisTemplate.opsForSet().add(userSessionsKey, newSession.getSessionId());
        stringRedisTemplate.expireAt(userSessionsKey, newSession.getHardExpiration());
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

        System.out.println("Deleting session with ID (String): " + sessionId);

        String hashedSessionId = hashUtility.hmacSha256(sessionId);

        Session session = findById(hashedSessionId);

        if(session == null) {
            throw new ResourceNotFoundException("Session not found");
        }

        String key = authProperties.getSessionPrefix() + hashedSessionId;
        sessionRedisTemplate.delete(key);

        String accountSessionsKey = authProperties.getAccountSessionPrefix() + session.getAccountId();
        stringRedisTemplate.opsForSet().remove(accountSessionsKey, hashedSessionId);
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
        String hashedSessionId = session.getSessionId();

        String key = authProperties.getSessionPrefix() + hashedSessionId;
        sessionRedisTemplate.delete(key);

        String accountSessionsKey = authProperties.getAccountSessionPrefix() + session.getAccountId();
        stringRedisTemplate.opsForSet().remove(accountSessionsKey, hashedSessionId);
    }

    @Override
    public void deleteAllByAccountId(UUID accountId) {

        List<Session> sessions = findAllByAccountId(accountId);
        for(Session session : sessions) {
            delete(session);
        }

    }

}
