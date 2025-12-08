package com.iforddow.authservice.application.listeners;

import com.iforddow.authservice.application.events.authentication.AuthenticationFailedEvent;
import com.iforddow.authservice.application.events.authentication.AuthenticationSuccessfulEvent;
import com.iforddow.authservice.auth.entity.entity.GeoLocation;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.entity.jpa.LoginAudit;
import com.iforddow.authservice.auth.repository.jpa.LoginAuditRepository;
import com.iforddow.authservice.common.service.GeoLocationService;
import com.iforddow.authservice.common.utility.HashUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuthenticationSuccessfulEvent(AuthenticationSuccessfulEvent event) {

        Account account = event.account();
        HttpServletRequest request = event.request();

        String ipAddress = request.getRemoteAddr();

        GeoLocation location =  geoLocationService.getLocation(ipAddress);

        LoginAudit loginAudit = LoginAudit.builder()
                .accountHash(hashUtility.hmacSha256(account.getId().toString()))
                .ipAddressHash(hashUtility.hmacSha256(request.getRemoteAddr()))
                .success(true)
                .city(location.getCity())
                .country(location.getCountry())
                .countryCode(location.getCountryCode())
                .region(location.getRegion())
                .createdAt(Instant.now())
                .build();

        loginAuditRepository.save(loginAudit);

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuthenticationFailedEvent(AuthenticationFailedEvent event) {

        Account account = event.account();
        HttpServletRequest request = event.request();

    }

}
