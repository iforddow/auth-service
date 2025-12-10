package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.application.events.authentication.AuthenticationFailedEvent;
import com.iforddow.authservice.application.events.authentication.AuthenticationSuccessfulEvent;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.factory.SessionFactory;
import com.iforddow.authservice.auth.request.LoginRequest;
import com.iforddow.authservice.auth.validator.AuthenticationValidator;
import com.iforddow.authservice.common.utility.DeviceType;
import com.iforddow.authsession.entity.Session;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * A service class for account login methods.
 *
 * @author IFD
 * @since 2025-10-27
 * */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final SessionFactory sessionFactory;
    private final AuthenticationValidator authenticationValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${session.cookie.name}")
    private String cookieName;

    /**
     * A method to handle account login.
     *
     * @param loginRequest The request object containing account login details.
     * @author IFD
     * @since 2025-10-27
     */
    @Transactional
    public String authenticate(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        Account account = null;

        try {
            account = authenticationValidator.validateAuthenticationRequest(loginRequest);

            // Create new session for the account
            Session newSession = sessionFactory.createAccountSession(account, request);

            // Create authentication token and set in security context
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    account.getId(), null, Collections.emptyList()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Handle session token based on device type
            if(loginRequest.getDeviceType().equals(DeviceType.WEB)) {

                // For web, set the session token in an HttpOnly cookie
                Cookie sessionCookie = new Cookie(cookieName, newSession.getSessionId());

                sessionCookie.setHttpOnly(true);
                sessionCookie.setPath("/");
                sessionCookie.setMaxAge(31536000);
                sessionCookie.setAttribute("SameSite", "Strict");
                sessionCookie.setSecure(true);

                response.addCookie(sessionCookie);

                return null;
            } else {
                return newSession.getSessionId();
            }
        } finally {
            if(account == null) {
                eventPublisher.publishEvent(new AuthenticationFailedEvent(account, request.getRemoteAddr()));
            } else {
                eventPublisher.publishEvent(new AuthenticationSuccessfulEvent(account, request.getRemoteAddr()));
            }
        }

    }

}
