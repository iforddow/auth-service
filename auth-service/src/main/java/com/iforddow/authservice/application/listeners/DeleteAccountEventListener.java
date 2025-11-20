package com.iforddow.authservice.application.listeners;

import com.iforddow.authservice.application.events.DeleteAccountEvent;
import com.iforddow.authservice.common.service.RabbitSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class DeleteAccountEventListener {

    private final RabbitSenderService rabbitSenderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeleteAccountEvent(DeleteAccountEvent event) {
        UUID accountId = event.accountId();

        // Send message to other services about account deletion
        rabbitSenderService.sendDeletedAccountMessage(accountId.toString());

        // Revoke all session tokens associated with the account
//        redisSessionTokenService.revokeAllTokensForAccount(accountId);
    }

}
