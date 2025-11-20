package com.iforddow.authservice.common.security;

import com.iforddow.authservice.auth.entity.entity.Session;
import com.iforddow.authservice.auth.repository.redis.SessionRepository;
import com.iforddow.authservice.common.exception.ResourceNotFoundException;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import com.iforddow.authservice.common.utility.SessionUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

/**
 * A filter that checks for Session in the Authorization header of HTTP requests.
 * If a valid Session is found, it authenticates the account and sets the security context.
 * This filter extends OncePerRequestFilter to ensure it is executed once per request.
 *
 * @author IFD
 * @since  2025-10-27
 * */
@RequiredArgsConstructor
@Slf4j
public class SessionFilter extends OncePerRequestFilter {

    private final SessionRepository sessionRepository;

    @Value("${session.ttl.seconds}")
    private long sessionTtlSeconds;

    SessionUtility sessionUtility = new SessionUtility();

    /**
     * A filter that intercepts HTTP requests to check for active Session in the Authorization header.
     * If a valid Session is found, it authenticates the account and sets the security context.
     *
     * @param request The HTTP request to filter.
     * @param response The HTTP response to filter.
     * @param filterChain The filter chain to continue processing the request.
     *
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException If an I/O error occurs during filtering.
     *
     * @author IFD
     * @since 2025-06-15
     * */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Get existing token from either cookie or header
        String sessionId = sessionUtility.validateIncomingSession(request);

        System.out.println("Session ID in Filter: " + sessionId);

        // If no existing token, return
        if(AuthServiceUtility.isNullOrEmpty(sessionId)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validate session existence
        Session session = sessionRepository.findById(sessionId).orElseThrow(
                () -> {

                    log.warn("Session id {} not found in repository", sessionId);
                    SecurityContextHolder.clearContext();

                    return new ResourceNotFoundException("Invalid session id");
                }
        );

        // Check for session hard expiry
        if(session.isHardExpired()) {
            log.warn("Session for account {} has hard expired", session.getAccountId());

            sessionRepository.delete(session);

            SecurityContextHolder.clearContext();

            filterChain.doFilter(request, response);
            return;
        }

        // Refresh session if expired (sliding expiration)
        if(session.isExpired()) {
            log.info("Refreshing session for account {}", session.getAccountId());

            SecurityContextHolder.clearContext();

            session = Session.refreshSession(session, Duration.ofSeconds(sessionTtlSeconds));
            sessionRepository.save(session);
        }

        // Set authentication in security context if not already set
        if(SecurityContextHolder.getContext().getAuthentication() == null) {

            // Set the authentication in the security context
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    session.getAccountId(), null, Collections.emptyList()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);

    }

}
