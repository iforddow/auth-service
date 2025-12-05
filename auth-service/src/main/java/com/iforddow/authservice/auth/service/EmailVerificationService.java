package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.common.exception.ResourceNotFoundException;
import com.iforddow.authservice.common.service.MailService;
import com.iforddow.authservice.common.utility.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Instant;

/**
* A service class for email verification methods.
*
* @author IFD
* @since 2025-12-04
* */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final AccountRepository accountRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final SpringTemplateEngine templateEngine;
    private final MailService mailService;

    @Value("${redis.email.verification.code.prefix}")
    private String verificationCodePrefix;

    @Value("${redis.email.verification.code.ttl.seconds}")
    private int verificationCodeTtlSeconds;

    @Value("${redis.email.verification.code.attempts.prefix}")
    private String verificationCodeAttemptsPrefix;

    @Value("${redis.email.verification.code.ttl.attempts.seconds}")
    private int verificationCodeAttemptsTtlSeconds;

    @Value("${auth.max.email.verification.code.requests.per.hour}")
    private int maxVerificationCodeRequestsPerHour;

    /**
    * A method to verify an email using a verification code.
    *
    * @param verificationCode The verification code to be validated.
    *
    * @author IFD
    * @since 2025-12-04
    * */
    public void verifyEmail(String email, String verificationCode) {

        String code = stringRedisTemplate.opsForValue().get(verificationCodePrefix + email);

        // If the code is invalid, throw an exception
        if (code == null || !code.equals(verificationCode)) {
            throw new ResourceNotFoundException("Invalid verification code");
        }

        Account account = accountRepository.findAccountByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Account with provided email not found"));
        account.setUserVerified(true);

        accountRepository.save(account);
    }

    /**
     * A method to send a new account email
     *
     * @author IFD
     * @since 2025-10-27
     * */
    public void sendVerificationEmail(String email) {

        Account account = accountRepository.findAccountByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Account with provided email not found"));

        //Check to make sure max attempts not exceeded
        String attemptsKey = verificationCodeAttemptsPrefix + email;
        String attemptsValue = stringRedisTemplate.opsForValue().get(attemptsKey);
        int attempts = attemptsValue != null ? Integer.parseInt(attemptsValue) : 0;

        if (maxVerificationCodeRequestsPerHour != -1) {
            if(attempts >= maxVerificationCodeRequestsPerHour) {
                throw new ResourceNotFoundException("Maximum verification email attempts exceeded. Please try again later.");
            }   else {
                attempts++;
                stringRedisTemplate.opsForValue().set(attemptsKey, String.valueOf(attempts));

                Long currentTtl = stringRedisTemplate.getExpire(attemptsKey);
                if (currentTtl == null || currentTtl == -1) {
                    stringRedisTemplate.expireAt(attemptsKey, Instant.now().plusSeconds(verificationCodeAttemptsTtlSeconds));
                }
            }
        }

        String verificationCode = createEmailVerificationCode(email);

        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        int ttl = verificationCodeTtlSeconds / 60;

        context.setVariable("ttl", ttl);

        String content = templateEngine.process("email/email-verification-code", context);

        mailService.sendMailTemplate(account.getEmail(), "Email Verification", content);
    }

    /**
     * A method to create and store an email verification code in Redis.
     *
     * @param email The email address to create the verification code for.
     *
     * @author IFD
     * @since 2025-12-04
     * */
    public String createEmailVerificationCode(String email) {
        String code = CodeGenerator.generateRandomCode();

        String key = verificationCodePrefix + email;

        stringRedisTemplate.opsForValue().set(key, code);
        stringRedisTemplate.expireAt(key, Instant.now().plusSeconds(verificationCodeTtlSeconds));

        return code;
    }

}
