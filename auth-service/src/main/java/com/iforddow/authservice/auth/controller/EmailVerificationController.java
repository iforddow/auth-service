package com.iforddow.authservice.auth.controller;

import com.iforddow.authservice.auth.request.VerifyEmailRequest;
import com.iforddow.authservice.auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
* A controller class to handle email verification endpoints.
*
* @author IFD
* @since 2025-12-04
* */
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
    * A mapping to send a verification email to the user.
    *
    * @param email The email address to send the verification code to.
    *
    * @author IFD
    * @since 2025-12-04
    * */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationEmail(@RequestParam String email) {
        emailVerificationService.sendVerificationEmail(email);
        return ResponseEntity.ok().build();
    }

    /**
    * A mapping to verify an email using a verification code.
    *
    * @param verifyEmailRequest The request object containing email and verification code.
    *
    * @author IFD
    * @since 2025-12-04
    * */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest verifyEmailRequest) {
        emailVerificationService.verifyEmail(verifyEmailRequest.getEmail(), verifyEmailRequest.getVerificationCode());
        return ResponseEntity.ok().build();
    }

}
