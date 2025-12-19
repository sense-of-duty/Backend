package org.example.gdgpage.repository.assignment;

import org.example.gdgpage.domain.assignment.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}
