package org.example.gdgpage.domain.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "submission_feedbacks", indexes = {
        @Index(name = "idx_feedback_submission", columnList = "submissionId"),
        @Index(name = "idx_feedback_author", columnList = "authorId")
})
public class SubmissionFeedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long submissionId;

    @Column(nullable = false)
    private Long authorId;

    @Lob
    @Column(nullable = false)
    private String content;

    public static SubmissionFeedback create(Long submissionId, Long authorId, String content) {
        return SubmissionFeedback.builder()
                .submissionId(submissionId)
                .authorId(authorId)
                .content(content)
                .build();
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
