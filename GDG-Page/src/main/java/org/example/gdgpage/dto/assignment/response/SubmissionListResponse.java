package org.example.gdgpage.dto.assignment.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubmissionListResponse(
        Long id,
        Long submitterId,
        String attachmentUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
