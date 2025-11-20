package com.iforddow.authservice.auth.entity.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

/**
* A JPA entity representing an audit record for user registrations.
* Each record captures various details about the registration event,
* including user and IP address hashes, geolocation data, device and browser information,
* and a timestamp of when the registration occurred.
*
* @author IFD
* @since 2025-11-09
* */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registration_audit")
public class RegistrationAudit {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "account_hash", nullable = false)
    private String accountHash;

    @Column(name = "ip_address_hash", nullable = false)
    private String ipAddressHash;

    @ColumnDefault("'Unknown'")
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @ColumnDefault("'Unknown'")
    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @ColumnDefault("'Unknown'")
    @Column(name = "device_type", nullable = false, length = 50)
    private String deviceType;

    @ColumnDefault("'Unknown'")
    @Column(name = "os_type", nullable = false, length = 50)
    private String osType;

    @ColumnDefault("'Unknown'")
    @Column(name = "os_version", nullable = false, length = 20)
    private String osVersion;

    @ColumnDefault("'Unknown'")
    @Column(name = "browser_type", nullable = false, length = 50)
    private String browserType;

    @ColumnDefault("'Unknown'")
    @Column(name = "browser_version", nullable = false, length = 20)
    private String browserVersion;

    @ColumnDefault("now()")
    @Column(name = "\"timestamp\"", nullable = false)
    private Instant timestamp;

    @ColumnDefault("'Unknown'")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @ColumnDefault("'Unknown'")
    @Column(name = "country_code", nullable = false, length = 25)
    private String countryCode;

}