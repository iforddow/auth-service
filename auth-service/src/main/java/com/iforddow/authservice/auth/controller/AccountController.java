package com.iforddow.authservice.auth.controller;

import com.iforddow.authservice.auth.service.DeleteAccountService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* A controller class to handle account-related endpoints.
*
* @author IFD
* @since 2025-12-04
* */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final DeleteAccountService deleteAccountService;

    /**
     * An endpoint for accessing the delete account method.
     *
     * @author IFD
     * @since 2025-10-29
     *
     */
    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAccount(HttpServletResponse response) {

        deleteAccountService.deleteAccount(response);

        return ResponseEntity.ok("Account deleted successfully.");
    }

}
