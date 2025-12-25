package org.example.gdgpage.dto.assignment.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubmissionResponse(
        Long id,
        Long assignmentId,
        Long submitterId,
        String content,
        String attachmentUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
