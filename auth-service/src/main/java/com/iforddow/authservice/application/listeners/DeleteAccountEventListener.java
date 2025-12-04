package com.iforddow.authservice.application.listeners;

import com.iforddow.authservice.application.events.DeleteAccountEvent;
import com.iforddow.authservice.auth.repository.redis.SessionRepositoryImpl;
import com.iforddow.authservice.common.service.RabbitSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

/**
* A listener class to handle account deletion events.
*
* @author IFD
* @since 2025-12-03
* */
@RequiredArgsConstructor
@Component
public class DeleteAccountEventListener {

    private final RabbitSenderService rabbitSenderService;
    private final SessionRepositoryImpl sessionRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeleteAccountEvent(DeleteAccountEvent event) {
        UUID accountId = event.accountId();

        // Send message to other services about account deletion
        rabbitSenderService.sendDeletedAccountMessage(accountId.toString());

        // Revoke all session tokens associated with the account
        sessionRepository.deleteAllByAccountId(accountId);
    }

}
