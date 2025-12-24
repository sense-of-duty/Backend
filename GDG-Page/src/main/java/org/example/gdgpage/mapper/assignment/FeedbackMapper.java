package org.example.gdgpage.mapper.assignment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.assignment.SubmissionFeedback;
import org.example.gdgpage.dto.assignment.response.FeedbackResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeedbackMapper {

    public static FeedbackResponse toResponse(SubmissionFeedback feedback) {
        if (feedback == null) {
            return null;
        }

        return FeedbackResponse.builder()
                .id(feedback.getId())
                .submissionId(feedback.getSubmissionId())
                .authorId(feedback.getAuthorId())
                .content(feedback.getContent())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }
}
