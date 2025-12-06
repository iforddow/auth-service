package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.factory.SessionFactory;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.request.LoginRequest;
import com.iforddow.authservice.common.exception.TooManyRequests;
import com.iforddow.authservice.common.utility.CheckMax;
import com.iforddow.authservice.common.utility.DeviceType;
import com.iforddow.authservice.auth.validator.CredentialValidator;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authservice.common.exception.InvalidCredentialsException;
import com.iforddow.authservice.common.exception.ResourceNotFoundException;
import com.iforddow.authsession.entity.Session;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * A service class for account login methods.
 *
 * @author IFD
 * @since 2025-10-27
 * */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final AccountRepository accountRepository;
    private final SessionFactory sessionFactory;
    private final CredentialValidator credentialValidator;
    private final CheckMax checkMax;
    private final StringRedisTemplate stringRedisTemplate;
    private final AccountLockService accountLockService;

    @Value("${session.cookie.name}")
    private String cookieName;

    // Properties for login attempt tracking
    @Value("${redis.login.attempt.counter.prefix}")
    private String loginAttemptCounterPrefix;

    @Value("${redis.login.attempt.counter.ttl.seconds}")
    private int loginAttemptCounterTtlSeconds;

    @Value("${auth.max.login.attempts}")
    private int maxLoginAttempts;

    @Value("${auth.lockout.duration.minutes}")
    private int lockoutDurationMinutes;

    /**
     * A method to handle account login.
     *
     * @param loginRequest The request object containing account login details.
     * @author IFD
     * @since 2025-10-27
     */
    public String authenticate(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        // Ensure device type is valid
        if(!(loginRequest.getDeviceType() == DeviceType.WEB) && !loginRequest.getDeviceType().equals(DeviceType.MOBILE)) {
            throw new BadRequestException("Invalid device type");
        }

        // Ensure account exists
        Account account = accountRepository.findAccountByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Account email not found")
        );

        // Check and handle account lock status
        if(account.getLocked()) {
            if(account.getLockedUntil() != null && Instant.now().isAfter(account.getLockedUntil())) {
                accountLockService.unlockAccount(account);
            }   else {
                Instant lockedUntil = account.getLockedUntil();

                throw new BadRequestException("Account is currently locked until: " + (lockedUntil != null ? lockedUntil.toString() : "INDEFINITE"));
            }
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

        // Create new session for the account
        Session newSession = sessionFactory.createAccountSession(account, request);

        // Create authentication token and set in security context
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                account.getId(), null, Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Handle session token based on device type
        if(loginRequest.getDeviceType().equals(DeviceType.WEB)) {

            // For web, set the session token in an HttpOnly cookie
            Cookie sessionCookie = new Cookie(cookieName, newSession.getSessionId());

            sessionCookie.setHttpOnly(true);
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(31536000);
            sessionCookie.setAttribute("SameSite", "Strict");
            sessionCookie.setSecure(true);

            response.addCookie(sessionCookie);

            return null;
        } else {
            return newSession.getSessionId();
        }

    }

}
