package org.example.gdgpage.repository.assignment;

import org.example.gdgpage.domain.assignment.AssignmentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    Optional<AssignmentSubmission> findByAssignmentIdAndSubmitterId(Long assignmentId, Long submitterId);
    Page<AssignmentSubmission> findAllByAssignmentId(Long assignmentId, Pageable pageable);
}
