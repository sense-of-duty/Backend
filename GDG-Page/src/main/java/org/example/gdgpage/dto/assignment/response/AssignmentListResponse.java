package org.example.gdgpage.dto.assignment.response;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record AssignmentListResponse(
        Long id,
        String title,
        LocalDateTime dueAt,
        Set<PartType> parts,
        Long authorId,
        LocalDateTime createdAt
) {}
