package com.iforddow.authservice.application.listeners;

import com.iforddow.authservice.application.events.RegistrationEvent;
import com.iforddow.authservice.auth.entity.entity.ClientInfo;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.entity.entity.GeoLocation;
import com.iforddow.authservice.auth.entity.jpa.RegistrationAudit;
import com.iforddow.authservice.auth.repository.jpa.RegistrationAuditRepository;
import com.iforddow.authservice.common.service.GeoLocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.Instant;

/*
* A listener class to handle adding registration audit records
* upon a new account registration.
*
* @author IFD
* @since 2025-11-09
* */
@RequiredArgsConstructor
@Component
@Slf4j
public class RegistrationAuditListener {

    private final GeoLocationService geoLocationService;
    private final RegistrationAuditRepository registrationAuditRepository;

    /**
     * A method to add a registration audit record after a successful account registration.
     *
     * @param registrationEvent The registration event containing account and request details.
     *
     * @author IFD
     * @since 2025-11-09
     * */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleRegistrationEvent(RegistrationEvent registrationEvent) {

        try {
            Account account = registrationEvent.account();
            HttpServletRequest request = registrationEvent.request();

            String ipAddress = request.getRemoteAddr();

            GeoLocation geoLocation = geoLocationService.getLocation(ipAddress);

            ClientInfo clientInfo = new ClientInfo(registrationEvent.request().getHeader("User-Agent"));

            // Create and save RegistrationAudit record
            RegistrationAudit registrationAudit = RegistrationAudit.builder()
                    .account(account)
                    .ipAddress(ipAddress)
                    .country(geoLocation.getCountry())
                    .countryCode(geoLocation.getCountryCode())
                    .region(geoLocation.getCity())
                    .city(geoLocation.getRegion())
                    .deviceType(clientInfo.getDeviceType())
                    .osType(clientInfo.getOsType())
                    .osVersion(clientInfo.getOsVersion())
                    .browserType(clientInfo.getBrowserType())
                    .browserVersion(clientInfo.getBrowserVersion())
                    .timestamp(Instant.now())
                    .build();

            registrationAuditRepository.save(registrationAudit);

            // Log success
            log.info("RegistrationAudit saved for account {}", account.getId());

        } catch (Exception e) {
            // Log any errors
            log.error("Failed to log registration audit: {}", e.getMessage());
        }
    }

}
