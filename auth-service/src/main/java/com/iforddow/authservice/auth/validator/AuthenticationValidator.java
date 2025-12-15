package com.iforddow.authservice.auth.validator;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.request.LoginRequest;
import com.iforddow.authservice.auth.service.AccountLockService;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authservice.common.exception.InvalidCredentialsException;
import com.iforddow.authservice.common.exception.ResourceNotFoundException;
import com.iforddow.authservice.common.exception.TooManyRequests;
import com.iforddow.authservice.common.utility.CheckMax;
import com.iforddow.authservice.common.utility.DeviceType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuthenticationValidator {

    private final AccountRepository accountRepository;
    private final AccountLockService accountLockService;
    private final CheckMax checkMax;
    private final StringRedisTemplate stringRedisTemplate;
    private final CredentialValidator credentialValidator;

    // Properties for login attempt tracking
    @Value("${redis.login.attempt.counter.prefix}")
    private String loginAttemptCounterPrefix;

    @Value("${redis.login.attempt.counter.ttl.seconds}")
    private int loginAttemptCounterTtlSeconds;

    @Value("${auth.max.login.attempts}")
    private int maxLoginAttempts;

    @Value("${auth.lockout.duration.minutes}")
    private int lockoutDurationMinutes;

    public Account validateAuthenticationRequest(LoginRequest loginRequest) throws BadRequestException, InvalidCredentialsException, ResourceNotFoundException, TooManyRequests {

        // Ensure device type is valid
        if(!(loginRequest.getDeviceType() == DeviceType.WEB) && !loginRequest.getDeviceType().equals(DeviceType.MOBILE)) {
            throw new BadRequestException("Invalid device type");
        }

        Account account;

        // Ensure account exists
        // Check to ensure the account doesn't exist
        if(accountRepository.findAccountsByEmail(loginRequest.getEmail()).isEmpty()) {
            throw new ResourceNotFoundException("Account email not found");
        } else if(accountRepository.findAccountsByEmail(loginRequest.getEmail()).size() > 1) {
            throw new BadRequestException("Multiple accounts found with the same email");
        } else {
            account = accountRepository.findAccountsByEmail(loginRequest.getEmail()).getFirst();
        }

        // Check and handle account lock status
        if(accountLockService.isAccountLocked(account)) {
            throw new TooManyRequests("Account is locked until: " + account.getLockedUntil().toString());
        }

        // Check for maximum login attempts
        String key = loginAttemptCounterPrefix + loginRequest.getEmail();

        // If max attempts reached, lock the account
        if(checkMax.getAttempts(key) >= maxLoginAttempts) {

            Instant lockTime = Instant.now().plus(Duration.ofMinutes(lockoutDurationMinutes));

            stringRedisTemplate.delete(key);
            accountLockService.lockAccount(account, lockTime);

            throw new TooManyRequests("Too many login attempts, account is now locked until: " + lockTime.toString());
        }

        // Validate credentials and handle login attempts
        if(!credentialValidator.validate(account, loginRequest.getPassword())) {

            checkMax.increaseAttempts(key, loginAttemptCounterTtlSeconds);

            throw new InvalidCredentialsException("Invalid credentials");
        }

        return account;
    }

}
