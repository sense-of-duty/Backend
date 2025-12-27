package org.example.gdgpage.dto.notice.response.post;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

import java.time.LocalDateTime;


@Builder
public record NoticeResponse(
        Long id,
        String title,
        String content,
        PartType partId,
        Long authorId,
        String authorName,
        int viewCount,
        boolean isPinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
