package com.iforddow.authservice.auth.entity.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "login_audit")
public class LoginAudit {
    @Id
    @Builder.Default
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "account_hash", nullable = false)
    private String accountHash;

    @Column(name = "ip_address_hash", nullable = false)
    private String ipAddressHash;

    @Builder.Default
    @ColumnDefault("'Unknown'")
    @Column(name = "country", nullable = false, length = 100)
    private String country = "Unknown";

    @Builder.Default
    @ColumnDefault("'Unknown'")
    @Column(name = "country_code", nullable = false, length = 25)
    private String countryCode = "Unknown";

    @Builder.Default
    @ColumnDefault("'Unknown'")
    @Column(name = "region", nullable = false, length = 100)
    private String region = "Unknown";

    @Builder.Default
    @ColumnDefault("'Unknown'")
    @Column(name = "city", nullable = false, length = 100)
    private String city = "Unknown";

    @Builder.Default
    @ColumnDefault("null")
    @Column(name = "asn", length = 50)
    private String asn = null;

    @Builder.Default
    @ColumnDefault("null")
    @Column(name = "device_id", length = 128)
    private String deviceId = null;

    @Builder.Default
    @ColumnDefault("false")
    @Column(name = "success", nullable = false)
    private Boolean success = false;

    @Builder.Default
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

}