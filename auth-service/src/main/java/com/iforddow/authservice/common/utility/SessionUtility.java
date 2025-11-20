package com.iforddow.authservice.common.utility;

import com.iforddow.authservice.common.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * A utility class for session-related operations.
 *
 * @author IFD
 * @since 2025-11-11
 * */
@Component
public class SessionUtility {

    @Value("${session.cookie.name}")
    private String cookieName;

    /**
     * A method to normalize a token by removing the "Bearer " prefix if present.
     *
     * @param header The header or token string to normalize.
     * @return The normalized token string.
     *
     * @author IFD
     * @since 2025-11-04
     * */
    private String getSessionIdFromHeader(String header) {
        if (header == null) return null;
        String value = header.trim();
        if (value.toLowerCase().startsWith("bearer ")) {
            return value.substring(7).trim();
        }
        return value;
    }

    /**
    * A method to extract the session ID from cookies.
    *
    * @param cookies The array of cookies from the HTTP request.
    *
    * @return The session ID if found, otherwise null.
    *
    * @author IFD
    * @since 2025-11-05
    * */
    private String getSessionIdFromCookie(Cookie[] cookies) {

        if(cookies == null) return null;

        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * A method to ensure that only one token is provided
     * either via cookie or Authorization header. If both or none
     * are provided, an exception is thrown. This helps as
     * mobile apps will send tokens in headers while web apps
     * will use cookies.
     *
     * @param request The HTTP servlet request containing cookies and headers.
     * @return The single token provided.
     *
     * @author IFD
     * @since 2025-11-05
     * */
    public String validateIncomingSession(HttpServletRequest request) {

        // Get cookies and Authorization header
        Cookie[] cookies = request.getCookies();
        String authHeader = request.getHeader("Authorization");

        // Extract session id from Authorization header
        String sessionIdFromHeader = getSessionIdFromHeader(authHeader);

        // Extract session id from cookies
        String sessionIdFromCookie = getSessionIdFromCookie(cookies);
        
        // If both are provided, check to see if they are the same, if not, throw exception
        if (!AuthServiceUtility.isNullOrEmpty(sessionIdFromHeader) && !AuthServiceUtility.isNullOrEmpty(sessionIdFromCookie)) {
            if(!sessionIdFromHeader.equals(sessionIdFromCookie)) {
                throw new BadRequestException("Multiple varying session tokens provided");
            }
        }

        // If the cookie session id is present, return it
        if (sessionIdFromCookie != null) {
            return sessionIdFromCookie;
        }

        // Return the header session, this cannot be null as
        // both cannot be null due to previous checks
        return sessionIdFromHeader;

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
