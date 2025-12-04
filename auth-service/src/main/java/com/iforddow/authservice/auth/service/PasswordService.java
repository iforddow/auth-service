package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.request.ChangePasswordRequest;
import com.iforddow.authservice.auth.request.ResetPasswordRequest;
import com.iforddow.authservice.auth.validator.PasswordValidator;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authservice.common.exception.PasswordValidationException;
import com.iforddow.authservice.common.exception.ResourceNotFoundException;
import com.iforddow.authservice.common.service.MailService;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import com.iforddow.authservice.common.utility.CodeGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

/**
* A service class for handling password-related operations.
*
* @author IFD
* @since 2025-12-03
* */
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final MailService mailService;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${redis.verification.code.prefix}")
    private String verificationCodePrefix;

    @Value("${redis.password.reset.code.ttl.seconds}")
    private int verificationCodeTtlSeconds;

    /**
    * A method to change the password of the currently authenticated account.
    *
    * @param changePasswordRequest The request object containing old and new password details.
    *
    * @author IFD
    * @since 2025-12-03
    * */
    @Transactional
    public void changeAccountPassword(ChangePasswordRequest changePasswordRequest) {

        // Get account ID from security context
        UUID accountId = AuthServiceUtility.getAuthentication();

        // Find account by ID
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Validate the new password
        ArrayList<String> errors = passwordValidator.validatePassword(changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword(), changePasswordRequest.getConfirmNewPassword());

        // If there are validation errors, throw an exception
        if(!errors.isEmpty()) {
            throw new PasswordValidationException(String.join(", ", errors));
        }

        // Verify that the old password matches the current password
        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(), account.getPassword())) {
            throw new BadRequestException("Old password is incorrect.");
        }

        // Update the account's password
        account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        // Save the updated account
        accountRepository.save(account);

    }

    /**
    * A method to initiate a password reset for the currently authenticated account.
    *
    * @author IFD
    * @since 2025-12-03
    * */
    public void initiatePasswordReset() {

        // Get account ID from security context
        UUID accountId = AuthServiceUtility.getAuthentication();

        //Get account by ID
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Generate verification code
        String generatedVerificationCode = CodeGenerator.generateVerificationCode();

        // Store verification code in Redis
        String key = verificationCodePrefix + account.getId();

        // If key already exists, delete it first
        if(stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.delete(key);
        }

        // Store the new code
        stringRedisTemplate.opsForValue().set(key, generatedVerificationCode);
        stringRedisTemplate.expireAt(key, Instant.now().plusSeconds(verificationCodeTtlSeconds));

        // Send password reset email
        mailService.sendPasswordResetCodeEmail(account.getEmail(), generatedVerificationCode, verificationCodeTtlSeconds);
    }

    /**
    * A method to validate a password reset code for the currently authenticated account.
    *
    * @param code The password reset code to validate.
    * @return true if the code is valid, false otherwise.
    *
    * @author IFD
    * @since 2025-12-03
    * */
    public boolean validatePasswordResetCode(String code) {

        // Get account ID from security context
        UUID accountId = AuthServiceUtility.getAuthentication();

        // Get stored code from Redis
        String key = verificationCodePrefix + accountId;

        String storedCode = stringRedisTemplate.opsForValue().get(key);

        // If no code is found or codes do not match, throw exception
        return storedCode != null && storedCode.equals(code);
    }

    /**
    * A method to reset the password of the currently authenticated account.
    *
    * @param resetPasswordRequest The request object containing the new password details.
    *
    * @author IFD
    * @since 2025-12-03
    * */
    @Transactional
    public void resetAccountPassword(ResetPasswordRequest resetPasswordRequest) {

        // Get account ID from security context
        UUID accountId = AuthServiceUtility.getAuthentication();

        // Validate the reset code
        if(!validatePasswordResetCode(resetPasswordRequest.getCode())) {
            throw new BadRequestException("Invalid or expired password reset code.");
        }

        // Find account by ID
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Get old password
        String oldPassword = account.getPassword();

        // Ensure new password is not the same as the old password
        if(passwordEncoder.matches(resetPasswordRequest.getNewPassword(), oldPassword)) {
            throw new BadRequestException("New password cannot be the same as the old password.");
        }

        // Validate the new password
        ArrayList<String> errors = passwordValidator.validatePassword(resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmNewPassword());

        // If there are validation errors, throw an exception
        if(!errors.isEmpty()) {
            throw new PasswordValidationException(String.join(", ", errors));
        }

        // Update the account's password
        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));

        // Save the updated account
        accountRepository.save(account);

    }

}
