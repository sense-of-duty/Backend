package org.example.gdgpage.repository.assignment;

import org.example.gdgpage.domain.assignment.SubmissionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionFeedbackRepository extends JpaRepository<SubmissionFeedback, Long> {
    List<SubmissionFeedback> findAllBySubmissionIdOrderByCreatedAtAsc(Long submissionId);
}
