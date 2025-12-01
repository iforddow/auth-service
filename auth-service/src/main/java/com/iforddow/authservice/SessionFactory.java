package com.iforddow.authservice;

import com.iforddow.authsession.entity.Session;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public class SessionFactory {

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

}
