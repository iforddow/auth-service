package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.application.events.DeleteAccountEvent;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.repository.redis.SessionRepositoryImpl;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authservice.common.exception.ResourceNotFoundException;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import com.iforddow.authsession.common.AuthProperties;
import com.iforddow.authsession.entity.Session;
import com.iforddow.authsession.utility.FilterUtility;
import com.iforddow.authsession.validator.SessionValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
* A service class for deleting accounts.
*
* @author IFD
* @since 2025-10-29
* */
@RequiredArgsConstructor
@Service
public class DeleteAccountService {

    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthProperties authProperties;


    /**
    * A method to delete an account by ID.
    *
    * @param response The HttpServletResponse variable
    *
    * @author IFD
    * @since 2025-10-29
    * */
    @Transactional
    public void deleteAccount(HttpServletResponse response) {

        UUID accountId = AuthServiceUtility.getAuthentication();

        // Find account by ID
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException("Account not found with id: " + accountId)
        );

        // Delete all sessions for the account
        accountRepository.delete(account);

        // Publish account deletion event
        eventPublisher.publishEvent(new DeleteAccountEvent(accountId));

        // Invalidate the session cookie
        Cookie cookie = new Cookie(authProperties.getCookieName(), null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(true);

        response.addCookie(cookie);

    }

}
