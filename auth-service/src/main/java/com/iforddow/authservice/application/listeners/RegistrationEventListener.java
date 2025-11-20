package com.iforddow.authservice.application.listeners;

import com.iforddow.authservice.application.events.RegistrationEvent;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.common.service.MailService;
import com.iforddow.authservice.common.service.RabbitSenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * A class that listens for registration events
 * and handles post-registration actions such as
 * sending notification emails and informing other services
 * via RabbitMQ.
 *
 * @author IFD
 * @since 2025-11-09
 * */
@RequiredArgsConstructor
@Component
@Slf4j
public class RegistrationEventListener {

    private final RabbitSenderService rabbitSenderService;
    private final MailService mailService;

    /**
     * A method that handles registration events after
     * the transaction has been committed. It sends a notification email
     * to the newly registered account and informs other services via
     * RabbitMQ. Failures in sending emails are logged but do not
     * interrupt the flow.
     *
     * @param event The registration event containing account details.
     *
     * @author IFD
     * @since 2025-11-09
     * */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRegistrationEvent(RegistrationEvent event) {

        Account account = event.account();

        // Send message to other services about new account — log failures but do not throw
        rabbitSenderService.sendNewAccountMessage(account.getId().toString());

        try {
            mailService.sendNewAccountEmail(account.getEmail(), "https://auth.iforddow.com/login");
            log.info("New account email sent successfully to accountId={} email={}", account.getId(), account.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send new account email to {}: {}", account.getEmail(), e.getMessage());

        } catch (MailException e) {
            log.error("Mail service error when sending new account email to {}: {}", account.getEmail(), e.getMessage());

        }

    }

}
