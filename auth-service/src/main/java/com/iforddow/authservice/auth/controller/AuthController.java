package com.iforddow.authservice.auth.controller;

import com.iforddow.authservice.auth.request.*;
import com.iforddow.authservice.auth.service.*;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import com.iforddow.authservice.common.utility.SessionUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * A controller class to handle authentication
 * endpoints.
 *
 * @author IFD
 * @since 2025-10-27
 *
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthController {

    private final RegisterService registerService;
    private final AuthenticationService authenticationService;
    private final DeleteAccountService deleteAccountService;
    private final LogoutService logoutService;
    private final PasswordService passwordService;

    SessionUtility sessionUtility = new SessionUtility();

    /**
     * An endpoint for accessing the registration method.
     *
     * @author IFD
     * @since 2025-10-28
     *
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {

        // Ensure there is only one session token from either cookie or header
        String existingToken = sessionUtility.validateIncomingSession(request);

        registerService.register(registerRequest, existingToken, request);

        return ResponseEntity.ok().build();

    }

    /**
     * An endpoint for accessing the authentication (login) method.
     *
     * @author IFD
     * @since 2025-10-28
     *
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        String loginResult = authenticationService.authenticate(loginRequest, request, response);

        return ResponseEntity.ok(loginResult);

    }

    /**
    * An endpoint for accessing the logout method.
    *
    * @author IFD
    * @since 2025-11-02
    * */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody LogoutRequest logoutRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        String existingToken = sessionUtility.validateIncomingSession(request);

        try {
            logoutService.logout(logoutRequest, existingToken, response);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * An endpoint for accessing the delete account method.
     *
     * @author IFD
     * @since 2025-10-29
     *
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@CookieValue(value = "${session.cookie.name}", required = false) String cookieValue,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if(!AuthServiceUtility.isNullOrEmpty(cookieValue)) {
                deleteAccountService.deleteAccount(cookieValue);
            } else if(!AuthServiceUtility.isNullOrEmpty(authHeader)) {
                deleteAccountService.deleteAccount(authHeader);
            } else {
                return ResponseEntity.badRequest().body("No authentication method provided");
            }

            return ResponseEntity.ok().body("Account deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
    * An endpoint to change an account's password.
    *
    * @author IFD
    * @since 2025-11-02
    * */
    @PostMapping("/change-password")
    public ResponseEntity<?> changeAccountPassword(
                                                   @RequestBody ChangePasswordRequest changePasswordRequest) {

        try {
            passwordService.changeAccountPassword(changePasswordRequest);
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to change password: " + e.getMessage());
        }

    }
}
