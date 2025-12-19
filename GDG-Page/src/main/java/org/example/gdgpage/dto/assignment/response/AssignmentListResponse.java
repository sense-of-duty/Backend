package org.example.gdgpage.dto.assignment.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AssignmentListResponse(
        Long id,
        String title,
        LocalDateTime dueAt,
        Long partId,
        Long authorId,
        LocalDateTime createdAt
) {}
