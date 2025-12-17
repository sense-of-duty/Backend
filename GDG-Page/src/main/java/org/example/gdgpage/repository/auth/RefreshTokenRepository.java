package org.example.gdgpage.repository.auth;

import org.example.gdgpage.domain.refresh.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String token);
    void deleteByRefreshToken(String token);
}
