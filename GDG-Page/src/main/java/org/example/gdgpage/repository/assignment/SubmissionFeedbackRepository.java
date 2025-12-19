package org.example.gdgpage.repository.assignment;

import org.example.gdgpage.domain.assignment.SubmissionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionFeedbackRepository extends JpaRepository<SubmissionFeedback, Long> {
    List<SubmissionFeedback> findAllBySubmissionIdOrderByCreatedAtAsc(Long submissionId);
    Optional<SubmissionFeedback> findByIdAndSubmissionId(Long id, Long submissionId);
}
