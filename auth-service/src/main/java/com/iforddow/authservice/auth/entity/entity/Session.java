package com.iforddow.authservice.auth.entity.entity;

import com.iforddow.authservice.common.utility.SessionUtility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
* A class representing a user session.
* It includes details such as session ID, account ID, creation time,
* IP address, user agent, expiration time, and hard expiration time.
*
* @author IFD
* @since 2025-11-09
* */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {

    private String sessionId;
    private UUID accountId;
    private Instant createdAt;
    private String ip;
    private String userAgent;
    private Instant expiresAt;
    private Instant hardExpiration;

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
                .sessionId(SessionUtility.generateSessionId())
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
    * A method to check if the session is expired.
    *
    * @return boolean
    *
    * @author IFD
    * @since 2025-11-09
    * */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
    * A method to check if the session is hard expired.
    *
    * @return boolean
    *
    * @author IFD
    * @since 2025-11-09
    * */
    public boolean isHardExpired() {
        return Instant.now().isAfter(hardExpiration);
    }

}
