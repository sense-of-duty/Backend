package org.example.gdgpage.repository.assignment;

import org.example.gdgpage.domain.assignment.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query("""
    select distinct a
    from Assignment a
    left join fetch a.parts
    order by a.createdAt desc
""")
    List<Assignment> findAllWithParts();
}
