package org.example.gdgpage.repository.assignment;

import org.example.gdgpage.domain.assignment.SubmissionFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionFeedbackRepository extends JpaRepository<SubmissionFeedback, Long> {
    Page<SubmissionFeedback> findAllBySubmissionId(Long submissionId, Pageable pageable);
    Optional<SubmissionFeedback> findByIdAndSubmissionId(Long id, Long submissionId);
}
