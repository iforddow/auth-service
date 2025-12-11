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
import com.iforddow.authservice.common.utility.HashUtility;
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
    private final HashUtility hashUtility;
    private final GeoLocationService geoLocationService;
    private final AccountRepository accountRepository;
    private final CookieUtility cookieUtility;
    private final GeoAsnService geoAsnService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuthenticationSuccessfulEvent(AuthenticationSuccessfulEvent event) {

        Account account = event.account();
        String ipAddress = event.ipAddress();
        GeoLocation location =  geoLocationService.getLocation(ipAddress);
        Instant currentTime = Instant.now();
        AsnInfo asnInfo = geoAsnService.lookup(ipAddress);

        LoginAudit loginAudit = LoginAudit.builder()
                .account(event.account())
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuthenticationFailedEvent(AuthenticationFailedEvent event) {

        Account account = event.account();
        String ipAddress = event.ipAddress();

    }

}
