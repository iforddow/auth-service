package com.iforddow.authservice.auth.controller;

import com.iforddow.authservice.auth.request.*;
import com.iforddow.authservice.auth.service.*;
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

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final LogoutService logoutService;

    /**
     * An endpoint for accessing the registration method.
     *
     * @author IFD
     * @since 2025-10-28
     *
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {

        registrationService.register(registerRequest, request);

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
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

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
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest, HttpServletRequest request, HttpServletResponse response) {

        logoutService.logout(logoutRequest, request, response);

        return ResponseEntity.ok().build();
    }
}
