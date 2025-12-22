package org.example.gdgpage.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.repository.auth.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.security.refresh-token-history-retention-days:3}")
    private long retentionDays;

    @Scheduled(
            cron = "${app.security.refresh-token-cleanup-cron:0 0 4 * * *}",
            zone = "Asia/Seoul"
    )
    @Transactional
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        refreshTokenRepository.deleteRevokedBefore(cutoff);
    }
}
