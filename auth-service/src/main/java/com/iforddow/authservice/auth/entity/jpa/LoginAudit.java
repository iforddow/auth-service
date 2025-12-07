package com.iforddow.authservice.auth.entity.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "login_audit")
public class LoginAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    @Column(name = "country_code", nullable = false, length = 25)
    private String countryCode;

    @ColumnDefault("'Unknown'")
    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @ColumnDefault("'Unknown'")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "asn", length = 50)
    private String asn;

    @Column(name = "device_id", length = 128)
    private String deviceId;

    @ColumnDefault("false")
    @Column(name = "success", nullable = false)
    private Boolean success = false;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}