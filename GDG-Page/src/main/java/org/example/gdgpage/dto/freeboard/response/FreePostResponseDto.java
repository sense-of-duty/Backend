package org.example.gdgpage.dto.freeboard.response;

import java.time.LocalDateTime;

public record FreePostResponseDto(
        Long id,
        String title,
        String content,
        Boolean isAnonymous,
        Boolean isPinned,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
