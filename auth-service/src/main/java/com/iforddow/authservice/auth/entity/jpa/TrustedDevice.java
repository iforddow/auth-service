package com.iforddow.authservice.auth.entity.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trusted_device")
public class TrustedDevice {
    @Id
    @GeneratedValue
    @Column(name = "device_id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "os_family", length = 100)
    private String osFamily;

    @Column(name = "browser_family", length = 100)
    private String browserFamily;

    @Column(name = "first_ip")
    private String firstIp;

    @Column(name = "last_ip")
    private String lastIp;

    @Column(name = "first_asn")
    private String firstAsn;

    @Column(name = "last_asn")
    private String lastAsn;

    @ColumnDefault("now()")
    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @ColumnDefault("false")
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}