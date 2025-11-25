package org.example.gdgpage.repository;

import org.example.gdgpage.domain.auth.OAuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
}
