package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.application.events.RegistrationEvent;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
* A service class for account registration.
*
* @author IFD
* @since 2025-10-27
* */
@RequiredArgsConstructor
@Service
public class RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * A method to handle account registration.
     *
     * @param registerRequest The request object containing account registration details.
     * @author IFD
     * @since 2025-06-14
     */
    @Transactional
    public void register(RegisterRequest registerRequest, HttpServletRequest httpRequest) {

        // If we get to this point, all validations have passed, and we
        // are ready to create the new account.

        // Create a new account
        Account account = Account.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        // Save the new account to the database
        accountRepository.save(account);

        // Publish an event to handle post-registration actions (will notify RabbitMQ and send verification email)
        eventPublisher.publishEvent(new RegistrationEvent(account, httpRequest));

    }


}
