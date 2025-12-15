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
    * A method to revoke a trusted device for an account.
    *
    * @param account The account to revoke the trusted device for.
    * @param trustedDeviceId The identifier of the device to revoke.
    *
    * @author IFD
    * @since 2025-12-12
    * */
    @Transactional
    public void revokeTrustedDevice(Account account, UUID trustedDeviceId) {
        if(account == null) {
            return;
        }

        TrustedDevice trustedDevice = trustedDeviceRepository.findTrustedDeviceById(trustedDeviceId);

        if(trustedDevice == null) {
            return;
        }

        if(!trustedDevice.getAccount().equals(account)) {
            return;
        }

        trustedDevice.setRevoked(true);
        trustedDevice.setRevokedAt(Instant.now());

        trustedDeviceRepository.save(trustedDevice);
    }

    /**
    * A method to revoke all trusted devices for an account.
    *
    * @param account The account to revoke all trusted devices for.
    *
    * @author IFD
    * @since 2025-12-12
    * */
    @Transactional
    public void revokeAllTrustedDevices(Account account) {
        if(account == null) {
            return;
        }

        List<TrustedDevice> trustedDevices = trustedDeviceRepository.findTrustedDevicesByAccount(account);

        if(trustedDevices == null || trustedDevices.isEmpty()) {
            return;
        }

        for(TrustedDevice trustedDevice : trustedDevices) {
            trustedDevice.setRevoked(true);
            trustedDevice.setRevokedAt(Instant.now());
            trustedDeviceRepository.save(trustedDevice);
        }
    }

    /**
    * A method to get all trusted devices for an account.
    *
    * @param account The account to get the trusted devices for.
    * @return A list of trusted devices for the account.
    *
    * @author IFD
    * @since 2025-12-12
    * */
    @Transactional
    public List<TrustedDevice> getTrustedDevicesForAccount(Account account) {
        if(account == null) {
            return List.of();
        }

        return trustedDeviceRepository.findTrustedDevicesByAccount(account);
    }

    /*
    * A method to delete a trusted device for an account.
    *
    * @param account The account to delete the trusted device for.
    * @param trustedDeviceId The identifier of the device to delete.
    *
    * @author IFD
    * @since 2025-12-12
    * */
    @Transactional
    public void deleteTrustedDevice(Account account, UUID trustedDeviceId) {
        if(account == null) {
            return;
        }

        TrustedDevice trustedDevice = trustedDeviceRepository.findTrustedDeviceById(trustedDeviceId);

        if(trustedDevice == null) {
            return;
        }

        if(!trustedDevice.getAccount().equals(account)) {
            return;
        }

        trustedDeviceRepository.delete(trustedDevice);
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

        TrustedDevice trustedDevice = trustedDeviceRepository.findTrustedDeviceById(trustedDeviceId);

        if(trustedDevice == null) {
            return false;
        }

        return trustedDevice.getAccount().equals(account) && !trustedDevice.getRevoked();
    }

}
