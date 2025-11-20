package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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


    /**
    * A method to delete an account by ID.
    *
    * @param sessionId The session ID.
    *
    * @author IFD
    * @since 2025-10-29
    * */
    @Transactional
    public void deleteAccount(String sessionId) {

//        String token = SessionUtility.extractIdFromHeader(sessionId);
//
//        if(!jwtService.validateJwtToken(token)) {
//            throw new BadRequestException("Invalid JWT token");
//        }
//
//        UUID accountId = UUID.fromString(jwtService.getAccountIdFromToken(token));
//
//        Account account = accountRepository.findById(accountId).orElseThrow(
//                () -> new ResourceNotFoundException("Account not found with id: " + accountId)
//        );
//
//        accountRepository.delete(account);
//
//        eventPublisher.publishEvent(new DeleteAccountEvent(accountId));

    }

}
