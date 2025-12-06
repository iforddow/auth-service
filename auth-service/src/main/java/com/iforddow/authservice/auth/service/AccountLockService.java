package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
* A service class for locking accounts. This will be expanded
* in the future to give admin level support. But for now, used
* in the authentication service to lock accounts after too many
* failed login attempts.
*
* @author IFD
* @since 2025-12-05
* */
@Service
@RequiredArgsConstructor
public class AccountLockService {

    private final AccountRepository accountRepository;

    /**
    * A method to lock an account until a specified time.
    *
    * @param account The account to be locked.
    * @param lockTime The time until which the account will be locked.
    *
    * @author IFD
    * @since 2025-12-05
    * */
    @Transactional
    public void lockAccount(Account account, Instant lockTime) {
        account.setLocked(true);
        account.setLockedUntil(lockTime);
        accountRepository.save(account);
    }

    /**
    * A method to unlock an account.
    *
    * @param account The account to be unlocked.
    *
    * @author IFD
    * @since 2025-12-05
    * */
    @Transactional
    public void unlockAccount(Account account) {
        account.setLocked(false);
        account.setLockedUntil(null);
        accountRepository.save(account);
    }
}
