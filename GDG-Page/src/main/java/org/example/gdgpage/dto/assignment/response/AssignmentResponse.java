package org.example.gdgpage.dto.assignment.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AssignmentResponse(
        Long id,
        String title,
        String content,
        LocalDateTime dueAt,
        Long partId,
        Long authorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
