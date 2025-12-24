package org.example.gdgpage.domain.refresh;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_user_device", columnList = "userId, deviceId"),
        @Index(name = "idx_refresh_user_tokenId", columnList = "userId, tokenId", unique = true)
}
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 80)
    private String deviceId;

    @Column(nullable = false, length = 64)
    private String tokenId;

    @Column(nullable = false, length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    private LocalDateTime lastUsedAt;

    @Column(length = 64)
    private String replacedByTokenId;

    public void markUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }

    public void revoke(String replacedByTokenId) {
        this.revoked = true;
        this.replacedByTokenId = replacedByTokenId;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
