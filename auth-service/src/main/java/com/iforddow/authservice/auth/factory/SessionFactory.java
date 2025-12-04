package com.iforddow.authservice.auth.factory;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.redis.SessionRepositoryImpl;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authsession.entity.Session;
import com.iforddow.authsession.utility.FilterUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionFactory {

    @Value("${session.ttl.seconds}")
    private long sessionTtlSeconds;

    @Value("${session.hard.expiry.seconds}")
    private long sessionHardExpirySeconds;

    @Value("${auth.max.sessions}")
    private int maxSessions;

    private final SessionRepositoryImpl sessionRepository;
    private final FilterUtility filterUtility;

    /**
     * A static factory method to create a new Session instance.
     *
     * @param accountId The ID of the account associated with the session.
     * @param ip The IP address from which the session was created.
     * @param userAgent The user agent string of the client.
     * @param ttl The time-to-live duration for the session.
     * @param hardExpiration The hard expiration duration for the session.
     *
     * @author IFD
     * @since 2025-11-09
     * */
    public static Session newSession(UUID accountId, String ip, String userAgent, Duration ttl, Duration hardExpiration) {

        Instant now = Instant.now();

        return Session.builder()
                .sessionId(generateSessionId())
                .accountId(accountId)
                .createdAt(now)
                .ip(ip)
                .userAgent(userAgent)
                .expiresAt(now.plus(ttl))
                .hardExpiration(now.plus(hardExpiration))
                .build();

    }

    /**
     * A static method to refresh an existing session.
     * It updates the expiration time based on the provided TTL.
     *
     * @param oldSession The existing session to be refreshed.
     * @param ttl The new time-to-live duration for the session.
     *
     * @author IFD
     * @since 2025-11-09
     * */
    public static Session refreshSession(Session oldSession, Duration ttl) {

        Instant now = Instant.now();

        return Session.builder()
                .sessionId(oldSession.getSessionId())
                .accountId(oldSession.getAccountId())
                .createdAt(oldSession.getCreatedAt())
                .ip(oldSession.getIp())
                .userAgent(oldSession.getUserAgent())
                .expiresAt(now.plus(ttl))
                .hardExpiration(oldSession.getHardExpiration())
                .build();

    }

    /**
     * A method to generate a secure random session ID.
     *
     * @return A securely generated random session ID as a URL-safe Base64 string.
     *
     * @author IFD
     * @since 2025-11-17
     * */
    public static String generateSessionId() {

        SecureRandom random = new SecureRandom();

        byte[] bytes = new byte[32];

        random.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    }

    /**
     * A method to create a session for the account
     * upon logging in and session refresh.
     *
     * @author IFD
     * @since 2025-10-27
     * */
    public Session createAccountSession(Account account, HttpServletRequest request) {

        String sessionId = filterUtility.getIncomingSessionId(request);

        if(sessionId != null) {
            throw new BadRequestException("Cannot create session when one already exists");
        }

        // Enforce maximum sessions per account
        List<Session> activeSessions = sessionRepository.findAllByAccountId(account.getId());

        // If over the limit, delete the oldest sessions
        if(activeSessions.size() > maxSessions) {
            // Keep deleting the oldest sessions until under the limit
            do {
                // Find the oldest session
                Session oldestSession = activeSessions.stream()
                        .min(Comparator.comparing(Session::getCreatedAt))
                        .orElseThrow(() -> new BadRequestException("Unable to enforce session limit"));

                // Delete the oldest session
                sessionRepository.delete(oldestSession.getSessionId());

            } while (activeSessions.size() > maxSessions);
        }

        // Get information required for the session
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        Duration ttl = Duration.ofSeconds(sessionTtlSeconds);
        Duration hardExpiry = Duration.ofSeconds(sessionHardExpirySeconds);

        // Create and save the new session
        Session session = newSession(account.getId(), ipAddress, userAgent, ttl, hardExpiry);

        sessionRepository.save(session);

        // Return the new session (with unhashed id)
        return session;
    }


}
