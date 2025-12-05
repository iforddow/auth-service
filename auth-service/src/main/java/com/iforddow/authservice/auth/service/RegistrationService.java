package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.application.events.RegistrationEvent;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.request.RegisterRequest;
import com.iforddow.authservice.common.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

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
    private final SpringTemplateEngine templateEngine;
    private final MailService mailService;

    @Value("${new.account.registration}")
    private String newAccountRegistrationSubject;

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

    /**
     * A method to send a new registration email
     *
     * @author IFD
     * @since 2025-10-27
     * */
    public void sendNewRegistrationEmail(String to, String verificationLink, String verificationCode) throws MessagingException, MailException {

        Context context = new Context();
        context.setVariable("verificationLink", verificationLink);
        context.setVariable("verificationCode", verificationCode);

        String content = templateEngine.process("email/new-account-email", context);

        mailService.sendMailTemplate(to, newAccountRegistrationSubject, content);
    }


}
