package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

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

        if(account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        account.setLocked(true);
        account.setLockedUntil(lockTime);
        accountRepository.save(account);
    }

    /**
    * A method to lock an account by its ID until a specified time.
    *
    * @param accountId The ID of the account to be locked.
    * @param lockTime The time until which the account will be locked.
    *
    * @author IFD
    * @since 2025-12-11
    * */
    @Transactional
    public void lockAccount(UUID accountId, Instant lockTime) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        lockAccount(account, lockTime);
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

        if(account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        account.setLocked(false);
        account.setLockedUntil(null);
        accountRepository.save(account);
    }

    /**
    * A method to unlock an account by its ID.
    *
    * @param accountId The ID of the account to be unlocked.
    *
    * @author IFD
    * @since 2025-12-11
    * */
    @Transactional
    public void unlockAccount(UUID accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found with id: " + accountId));

        unlockAccount(account);
    }

    /*
    * A method to unlock an account by its ID.
    *
    * @param accountId The ID of the account to be unlocked.
    *
    * @author IFD
    * @since 2025-12-11
    * */

    /**
    * A method to check if an account is locked. And if the lock time has expired,
    * unlock the account.
    *
    * @param account The account to check.
    * @return true if the account is locked, false otherwise.
    *
    * @author IFD
    * @since 2025-12-05
    * */
    @Transactional
    public boolean isAccountLocked(Account account) {
        if(account.getLocked()) {
            if(account.getLockedUntil() != null && Instant.now().isAfter(account.getLockedUntil())) {
                unlockAccount(account);
                return false;
            }
            return true;
        }
        return false;
    }
}
