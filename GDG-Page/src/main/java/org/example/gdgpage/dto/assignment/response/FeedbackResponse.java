package org.example.gdgpage.dto.assignment.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FeedbackResponse(
        Long id,
        Long submissionId,
        Long authorId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
