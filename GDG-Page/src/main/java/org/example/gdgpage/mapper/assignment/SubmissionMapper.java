package org.example.gdgpage.mapper.assignment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.assignment.AssignmentSubmission;
import org.example.gdgpage.dto.assignment.response.SubmissionListResponse;
import org.example.gdgpage.dto.assignment.response.SubmissionResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubmissionMapper {

    public static SubmissionResponse toResponse(AssignmentSubmission submission) {
        if (submission == null) {
            return null;
        }

        return SubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignmentId())
                .submitterId(submission.getSubmitterId())
                .content(submission.getContent())
                .attachmentUrl(submission.getAttachmentUrl())
                .createdAt(submission.getCreatedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }

    public static SubmissionListResponse toListResponse(AssignmentSubmission submission) {
        if (submission == null) {
            return null;
        }

        return SubmissionListResponse.builder()
                .id(submission.getId())
                .submitterId(submission.getSubmitterId())
                .attachmentUrl(submission.getAttachmentUrl())
                .createdAt(submission.getCreatedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }
}
