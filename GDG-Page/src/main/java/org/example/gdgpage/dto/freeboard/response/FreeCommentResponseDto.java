package org.example.gdgpage.dto.freeboard.response;

import java.time.LocalDateTime;

public record FreeCommentResponseDto(
        Long id,
        String content,
        Boolean isAnonymous,
        String authorName,
        Integer likeCount,
        Long parentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
