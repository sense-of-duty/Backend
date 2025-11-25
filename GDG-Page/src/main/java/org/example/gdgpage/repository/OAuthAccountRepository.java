package org.example.gdgpage.repository;

import org.example.gdgpage.domain.auth.OAuthAccount;
import org.example.gdgpage.domain.auth.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByProviderAndProviderId(Provider provider, String providerId);
    Optional<OAuthAccount> findByProviderAndProviderEmail(Provider provider, String providerEmail);
}
