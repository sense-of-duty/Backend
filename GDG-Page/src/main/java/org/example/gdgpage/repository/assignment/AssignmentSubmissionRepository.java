package org.example.gdgpage.repository.assignment;

import org.example.gdgpage.domain.assignment.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findAllByAssignmentIdOrderByCreatedAtDesc(Long assignmentId);
    Optional<AssignmentSubmission> findByAssignmentIdAndSubmitterId(Long assignmentId, Long submitterId);
}
