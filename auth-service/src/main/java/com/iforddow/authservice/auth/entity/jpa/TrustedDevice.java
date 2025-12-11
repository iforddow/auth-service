package com.iforddow.authservice.auth.entity.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Builder.Default
    @Column(name = "device_type", length = 50)
    private String deviceType = null;

    @Builder.Default
    @Column(name = "os_family", length = 100)
    private String osFamily = null;

    @Builder.Default
    @Column(name = "browser_family", length = 100)
    private String browserFamily = null;

    @Builder.Default
    @Column(name = "first_ip")
    private String firstIp = null;

    @Builder.Default
    @Column(name = "last_ip")
    private String lastIp = null;

    @Builder.Default
    @Column(name = "first_asn")
    private String firstAsn = null;

    @Builder.Default
    @Column(name = "last_asn")
    private String lastAsn = null;

    @Builder.Default
    @ColumnDefault("now()")
    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt = Instant.now();

    @Builder.Default
    @ColumnDefault("false")
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Builder.Default
    @Column(name = "revoked_at")
    private Instant revokedAt = Instant.now();

    @Builder.Default
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

}