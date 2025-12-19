package org.example.gdgpage.domain.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "assignment_submissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_submission_assignment_submitter", columnNames = {"assignmentId", "submitterId"})
        },
        indexes = {
                @Index(name = "idx_submission_assignment", columnList = "assignmentId"),
                @Index(name = "idx_submission_submitter", columnList = "submitterId")
        }
)
public class AssignmentSubmission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long assignmentId;

    @Column(nullable = false)
    private Long submitterId;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 500)
    private String attachmentUrl;

    public static AssignmentSubmission create(Long assignmentId, Long submitterId, String content, String attachmentUrl) {
        return AssignmentSubmission.builder()
                .assignmentId(assignmentId)
                .submitterId(submitterId)
                .content(content)
                .attachmentUrl(attachmentUrl)
                .build();
    }

    public void resubmit(String content, String attachmentUrl) {
        this.content = content;
        this.attachmentUrl = attachmentUrl;
    }
}
