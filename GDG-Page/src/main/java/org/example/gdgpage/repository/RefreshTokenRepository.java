package org.example.gdgpage.repository;

import org.example.gdgpage.domain.refresh.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
