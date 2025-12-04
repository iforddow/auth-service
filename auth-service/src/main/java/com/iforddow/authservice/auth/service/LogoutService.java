package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.repository.redis.SessionRepositoryImpl;
import com.iforddow.authservice.auth.request.LogoutRequest;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import com.iforddow.authsession.common.AuthProperties;
import com.iforddow.authsession.entity.Session;
import com.iforddow.authsession.utility.FilterUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
* A service class to log an account out of the application.
* Will revoke credentials.
*
* @author IFD
* @since 2025-10-27
* */
@RequiredArgsConstructor
@Service
public class LogoutService {

    private final FilterUtility filterUtility;
    private final SessionRepositoryImpl sessionRepository;
    private final AuthProperties authProperties;

    /**
     * A method to handle account logout.
     *
     * @param logoutRequest Logout request settings
     * @param response HttpServletResponse variable
     * @author IFD
     * @since 2025-10-27
     */
    public void logout(LogoutRequest logoutRequest, HttpServletRequest request, HttpServletResponse response) {

        // Use try-finally to ensure cookie is removed
        try {

            String sessionId = filterUtility.getIncomingSessionId(request);

            // Check user has valid session
            if (AuthServiceUtility.isNullOrEmpty(sessionId)) {
                throw new BadRequestException("No session found.");
            }

            Session currentSession = sessionRepository.findById(sessionId);

            // If no session found, throw an exception
            if(currentSession == null) {
                throw new BadRequestException("Invalid session token.");
            }

            // If all devices is true, revoke all tokens for the account (logout from all devices)
            if (logoutRequest.isAllDevices()) {

                // Get account ID from the session token
                UUID accountId = currentSession.getAccountId();

                // If no account ID found, throw an exception
                if(accountId == null) {
                    throw new BadRequestException("Could not find account for the provided token.");
                }

                sessionRepository.deleteAllByAccountId(accountId);
            } else {
                // Revoke only the current session token
                sessionRepository.delete(currentSession);
            }

        } finally {

            // Invalidate the session token by setting it to null
            // This will remove the cookie from the client side
            // no matter what happens in the try block.
            //
            // If the account is on mobile this cookie won't exist,
            // but this won't cause any issues.

            Cookie cookie = new Cookie(authProperties.getCookieName(), null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setSecure(true);

            response.addCookie(cookie);

        }

    }

}
