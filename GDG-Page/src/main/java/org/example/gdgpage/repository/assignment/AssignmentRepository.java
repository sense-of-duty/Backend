package org.example.gdgpage.repository.assignment;

import org.example.gdgpage.domain.assignment.Assignment;
import org.example.gdgpage.domain.auth.PartType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @EntityGraph(attributePaths = "parts")
    Page<Assignment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "parts")
    @Query("""
        select a
        from Assignment a
        where (a.parts is empty) or (:part in elements(a.parts))
    """)
    Page<Assignment> findVisibleByPart(PartType part, Pageable pageable);

    @EntityGraph(attributePaths = "parts")
    Optional<Assignment> findById(Long id);
}
