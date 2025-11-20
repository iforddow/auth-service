package com.iforddow.authservice.application.listeners;

import com.iforddow.authservice.application.events.RegistrationEvent;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.entity.entity.GeoLocation;
import com.iforddow.authservice.auth.entity.jpa.RegistrationAudit;
import com.iforddow.authservice.auth.repository.jpa.RegistrationAuditRepository;
import com.iforddow.authservice.common.service.GeoLocationService;
import com.iforddow.authservice.common.utility.HashUtility;
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

    private final HashUtility hashUtility;
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

            // Hashed account ID
            String accountHash = hashUtility.hmacSha256(account.getId().toString());

            String ipAddress = request.getRemoteAddr();

            GeoLocation geoLocation = geoLocationService.getLocation(ipAddress);

            // Get IP address
            String hashedIp = hashUtility.hmacSha256(ipAddress);

            // Parse User-Agent and get required info
            Parser uaParser = new Parser();
            Client agent = uaParser.parse(registrationEvent.request().getHeader("User-Agent"));

            String deviceType = agent.device.family;
            String osType = agent.os.family;
            String osVersion = agent.os.major + "." + agent.os.minor + "." + agent.os.patch;
            String browserType = agent.userAgent.family;
            String browserVersion = agent.userAgent.major + "." + agent.userAgent.minor + "." + agent.userAgent.patch;

            // Create and save RegistrationAudit record
            RegistrationAudit registrationAudit = RegistrationAudit.builder()
                    .accountHash(accountHash)
                    .ipAddressHash(hashedIp)
                    .country(geoLocation.getCountry())
                    .countryCode(geoLocation.getCountryCode())
                    .region(geoLocation.getCity())
                    .city(geoLocation.getRegion())
                    .deviceType(deviceType)
                    .osType(osType)
                    .osVersion(osVersion)
                    .browserType(browserType)
                    .browserVersion(browserVersion)
                    .timestamp(Instant.now())
                    .build();

            registrationAuditRepository.save(registrationAudit);

            // Log success
            log.info("RegistrationAudit saved for account {}", accountHash);

        } catch (Exception e) {
            // Log any errors
            log.error("Failed to log registration audit: {}", e.getMessage());
        }
    }

}
