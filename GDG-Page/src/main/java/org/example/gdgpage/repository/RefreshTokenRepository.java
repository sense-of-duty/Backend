package org.example.gdgpage.repository;

import org.example.gdgpage.domain.refresh.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserIdAndTokenId(Long userId, String tokenId);
    void deleteAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.userId = :userId and rt.deviceId = :deviceId and rt.revoked = false")
    int revokeAllActiveByUserAndDevice(@Param("userId") Long userId, @Param("deviceId") String deviceId);

    @Transactional
    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.userId = :userId and rt.revoked = false")
    int revokeAllActiveByUser(@Param("userId") Long userId);
}
