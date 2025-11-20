package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.entity.entity.Session;
import com.iforddow.authservice.auth.repository.redis.SessionRepository;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import com.iforddow.authservice.common.utility.SessionUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A service class for the token service in the application.
 * Will provide session token and token creation methods.
 *
 *
 * @author IFD
 * @since 2025-10-27
 * */
@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    SessionUtility sessionUtility = new SessionUtility();

    @Value("${session.ttl.seconds}")
    private long sessionTtlSeconds;

    @Value("${session.hard.expiry.seconds}")
    private long sessionHardExpirySeconds;

    @Value("${auth.max.sessions}")
    private int maxSessions;

    /**
     * A method to create a session for the account
     * upon logging in and session refresh.
     *
     * @author IFD
     * @since 2025-10-27
     * */
    public Session createSession(Account account, HttpServletRequest request) {

        String currentSessionId = sessionUtility.validateIncomingSession(request);

        if(currentSessionId != null) {
            throw new BadRequestException("Cannot create a new session while another session token is present");
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
                sessionRepository.delete(oldestSession.getSessionId(), account.getId());

            } while (activeSessions.size() > maxSessions);
        }

        // Get information required for the session
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        Duration ttl = Duration.ofSeconds(sessionTtlSeconds);
        Duration hardExpiry = Duration.ofSeconds(sessionHardExpirySeconds);

        // Create and save the new session
        Session session = Session.newSession(account.getId(), ipAddress, userAgent, ttl, hardExpiry);

        sessionRepository.save(session);

        // Return the new session
        return session;
    }

}
