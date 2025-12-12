package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.entity.entity.ClientInfo;
import com.iforddow.authservice.auth.entity.jpa.Account;
import com.iforddow.authservice.auth.entity.jpa.TrustedDevice;
import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.repository.jpa.TrustedDeviceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
* A service class for managing trusted devices.
*
* @author IFD
* @since 2025-12-11
* */
@Service
@RequiredArgsConstructor
public class TrustedDeviceService {

    private final TrustedDeviceRepository trustedDeviceRepository;
    private final AccountRepository accountRepository;

    /**
    * A method to add a trusted device for an account.
    *
    * @param account The account to which the trusted device will be added.
    *
    * @author IFD
    * @since 2025-12-11
    * */
    @Transactional
    public void addTrustedDevice(Account account, HttpServletRequest request, HttpServletResponse response) {

        if(account == null) {
            return;
        }

        ClientInfo clientInfo = new ClientInfo(request.getHeader("User-Agent"));

        TrustedDevice trustedDevice = TrustedDevice.builder()
                .account(account)
                .deviceType(clientInfo.getDeviceType())
                .osType(clientInfo.getOsType())
                .osVersion(clientInfo.getOsVersion())
                .browserType(clientInfo.getBrowserType())
                .browserVersion(clientInfo.getBrowserVersion())
                .firstIp(request.getRemoteAddr())
                .lastIp(request.getRemoteAddr())
                .firstAsn("")
                .lastAsn("")
                .lastSeenAt(Instant.now())
                .revoked(false)
                .revokedAt(null)
                .createdAt(Instant.now())
                .build();

    }

    /**
    * A method to check if a device is trusted for an account.
    *
    * @param account The account to check the trusted device for.
    * @param trustedDeviceId The identifier of the device to check.
    *
    * @author IFD
    * @since 2025-12-11
    * */
    public boolean isDeviceTrusted(Account account, UUID trustedDeviceId) {

        if(account == null) {
            return false;
        }

        List<TrustedDevice> trustedDevices = trustedDeviceRepository.findTrustedDeviceByAccount(account);

        if(trustedDevices == null || trustedDevices.isEmpty()) {
            return false;
        }

        for(TrustedDevice trustedDevice : trustedDevices) {
            if(trustedDevice.getId().equals(trustedDeviceId)) {
                return true;
            }
        }

        return false;
    }

}
