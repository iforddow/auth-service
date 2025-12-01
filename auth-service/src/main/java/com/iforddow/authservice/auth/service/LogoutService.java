package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.request.LogoutRequest;
import com.iforddow.authservice.common.utility.HashUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final HashUtility hashUtility;

    /**
     * A method to handle account logout.
     *
     * @param logoutRequest Logout request settings
     * @param response HttpServletResponse variable
     * @author IFD
     * @since 2025-10-27
     */
    public void logout(LogoutRequest logoutRequest, HttpServletResponse response) {

        // Use try-finally to ensure cookie is removed
        try {

//            if (AuthServiceUtility.isNullOrEmpty(existingToken)) {
//                throw new BadRequestException("No authentication session found.");
//            }
//
//            String hashedSessionToken = hashUtility.hmacSha256(existingToken);
//
//            // If all devices is true, revoke all tokens for the account (logout from all devices)
//            if (logoutRequest.isAllDevices()) {
//
//                // Get account ID from the session token
//                UUID accountId = redisSessionTokenService.getAccountIdFromToken(hashedSessionToken);
//
//                // If no account ID found, throw an exception
//                if(accountId == null) {
//                    throw new BadRequestException("Could not find account for the provided token.");
//                }
//
//                // Revoke all session tokens for the account
//                redisSessionTokenService.revokeAllTokensForAccount(accountId);
//            } else {
//
//                // Revoke only the current session token
//                redisSessionTokenService.revokeToken(hashedSessionToken);
//            }

        } finally {

            // Invalidate the session token by setting it to null
            // This will remove the cookie from the client side
            // no matter what happens in the try block.
            //
            // If the account is on mobile this cookie won't exist,
            // but this won't cause any issues.

//            Cookie cookie = new Cookie(cookieName, null);
//            cookie.setHttpOnly(true);
//            cookie.setPath("/");
//            cookie.setMaxAge(0);
//            cookie.setSecure(true);
//
//            response.addCookie(cookie);

        }

    }

}
