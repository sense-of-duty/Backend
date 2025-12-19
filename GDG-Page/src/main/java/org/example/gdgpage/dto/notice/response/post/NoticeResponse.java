package org.example.gdgpage.dto.notice.response.post;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record NoticeResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        int viewCount,
        boolean isPinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}