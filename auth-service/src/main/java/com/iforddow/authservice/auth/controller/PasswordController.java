package com.iforddow.authservice.auth.controller;

import com.iforddow.authservice.auth.request.ChangePasswordRequest;
import com.iforddow.authservice.auth.request.ResetPasswordRequest;
import com.iforddow.authservice.auth.service.PasswordService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    /**
     * An endpoint to change an account's password.
     *
     * @author IFD
     * @since 2025-11-02
     * */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeAccountPassword(@RequestBody ChangePasswordRequest changePasswordRequest) {

        passwordService.changeAccountPassword(changePasswordRequest);

        return ResponseEntity.ok().body("Password changed successfully");
    }

    /**
     * A method to initiate a password reset for the authenticated user.
     *
     * @author IFD
     * @since 2025-12-03
     * */
    @PostMapping("/init-reset-password")
    public ResponseEntity<?> initiatePasswordReset(@RequestParam String email) {

        passwordService.initiatePasswordReset(email);

        return ResponseEntity.ok().body("Password reset initiated successfully");
    }

    /*
     * A method to validate a password reset code.
     *
     * @author IFD
     * @since 2025-12-03
     * */
    @PostMapping("/validate-reset-password-code")
    public ResponseEntity<@NonNull Boolean> validatePasswordResetCode(@RequestBody String code) {

        boolean valid = passwordService.validatePasswordResetCode(code);

        return ResponseEntity.ok(valid);
    }

    /*
     * A method to reset the password using a valid reset code.
     *
     * @author IFD
     * @since 2025-12-03
     * */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        passwordService.resetAccountPassword(resetPasswordRequest);

        return ResponseEntity.ok().body("Password has been reset successfully");
    }

}
