package com.iforddow.authservice.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
* A mail service class to initialize sending emails
* from the app.
*
* @author IFD
* @since 2025-10-27
* */
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${mail.sender}")
    private String emailSender;

    /**
    * A method to send a new account email
    *
    * @author IFD
    * @since 2025-10-27
    * */
    public void sendNewAccountEmail(String to, String verificationLink) throws MessagingException, MailException {

        Context context = new Context();
        context.setVariable("verificationLink", verificationLink);

        String content = templateEngine.process("email/new-account-email", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject("Welcome to AuthService - Verify Your Email");
        helper.setFrom(emailSender);
        helper.setText(content, true);

        mailSender.send(mimeMessage);

    }

    /**
    * A method to send an email
    *
    * @author IFD
    * @since 2025-10-27
    * */
    public void sendMail(String to, String subject, String content) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(emailSender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);

    }

}
