package org.example.gdgpage.dto.notice.response;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;
import java.time.LocalDateTime;

@Builder
public record NoticeListResponse(
        Long id,
        String title,
        Long authorId,
        PartType partId,
        int viewCount,
        boolean isPinned,
        LocalDateTime createdAt
) {
}