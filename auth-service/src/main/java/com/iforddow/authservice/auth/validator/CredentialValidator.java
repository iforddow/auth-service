package com.iforddow.authservice.auth.validator;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authservice.common.exception.ResourceNotFoundException;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CredentialValidator {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean validate(Account account, String password) {

        if(account == null) {
            throw new ResourceNotFoundException("Account was not found");
        }

        if(AuthServiceUtility.isNullOrEmpty(password)) {
            throw new BadRequestException("Password cannot be null or empty");
        }

        String storedHash = account.getPassword();

        return passwordEncoder.matches(password, storedHash);

    }

}
