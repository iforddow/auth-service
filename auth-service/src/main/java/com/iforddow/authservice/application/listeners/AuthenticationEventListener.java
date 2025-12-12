package com.iforddow.authservice.application.listeners;

import com.iforddow.authservice.application.events.authentication.AuthenticationFailedEvent;
import com.iforddow.authservice.application.events.authentication.AuthenticationSuccessfulEvent;
import com.iforddow.authservice.auth.entity.entity.GeoLocation;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.entity.jpa.LoginAudit;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.repository.jpa.LoginAuditRepository;
import com.iforddow.authservice.common.records.AsnInfo;
import com.iforddow.authservice.common.service.GeoAsnService;
import com.iforddow.authservice.common.service.GeoLocationService;
import com.iforddow.authservice.common.utility.CookieUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationEventListener {

    private final LoginAuditRepository loginAuditRepository;
    private final GeoLocationService geoLocationService;
    private final AccountRepository accountRepository;
    private final CookieUtility cookieUtility;
    private final GeoAsnService geoAsnService;

    /**
    * A method to handle authentication successful events.
    *
    * @param event The AuthenticationSuccessfulEvent containing details of the successful authentication attempt.
    *
    * @author IFD
    * @since 2025-12-11
    * */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuthenticationSuccessfulEvent(AuthenticationSuccessfulEvent event) {

        Account account = event.account();
        String ipAddress = event.ipAddress();

        addAuditRecord(account, ipAddress, true);
    }

    /**
    * A method to handle authentication failed events.
    *
    * @param event The AuthenticationFailedEvent containing details of the failed authentication attempt.
    *
    * @author IFD
    * @since 2025-12-11
    * */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuthenticationFailedEvent(AuthenticationFailedEvent event) {

        Account account = event.account();
        String ipAddress = event.ipAddress();

        addAuditRecord(account, ipAddress, false);

    }

    /**
    * A method to add an audit record for login attempts.
    *
    * @param account The account associated with the login attempt.
    * @param ipAddress The IP address from which the login attempt was made.
    * @param success A boolean indicating whether the login attempt was successful.
    *
    * @author IFD
    * @since 2025-12-11
    * */
    public void addAuditRecord(Account account, String ipAddress, boolean success) {

        GeoLocation location =  geoLocationService.getLocation(ipAddress);
        Instant currentTime = Instant.now();
        AsnInfo asnInfo = geoAsnService.lookup(ipAddress);

        LoginAudit loginAudit = LoginAudit.builder()
                .account(account)
                .ipAddress(ipAddress)
                .success(true)
                .city(location.getCity())
                .country(location.getCountry())
                .countryCode(location.getCountryCode())
                .region(location.getRegion())
                .createdAt(currentTime)
                .asnNum(asnInfo.autonomousSystemNumber())
                .asnOrg(asnInfo.autonomousSystemOrganization())
                .deviceId("")
                .build();

        account.setLastLogin(currentTime);

        accountRepository.save(account);
        loginAuditRepository.save(loginAudit);

    }

}
