package com.iforddow.authservice.application.listeners;

import com.iforddow.authservice.application.events.authentication.AuthenticationFailedEvent;
import com.iforddow.authservice.application.events.authentication.AuthenticationSuccessfulEvent;
import com.iforddow.authservice.auth.entity.jpa.Account;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuthenticationSuccessfulEvent(AuthenticationSuccessfulEvent event) {

        Account account = event.account();
        HttpServletRequest request = event.request();

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuthenticationFailedEvent(AuthenticationFailedEvent event) {

        Account account = event.account();
        HttpServletRequest request = event.request();

    }

}
