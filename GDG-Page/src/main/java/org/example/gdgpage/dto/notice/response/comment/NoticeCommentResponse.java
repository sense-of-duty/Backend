package org.example.gdgpage.dto.notice.response.comment;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NoticeCommentResponse(
        Long id,
        String content,
        Long authorId,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<NoticeCommentResponse> children
) {
}
