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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "login_audit")
public class LoginAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

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

    @Builder.Default
    @ColumnDefault("'Unknown'")
    @Column(name = "asn_num", nullable = false, length = 50)
    private String asnNum = "Unknown";

    @Builder.Default
    @ColumnDefault("'Unknown'")
    @Column(name = "asn_org", nullable = false)
    private String asnOrg = "Unknown";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account", nullable = false)
    private Account account;

    @Column(name = "ip_address", nullable = false, length = 100)
    private String ipAddress;

}