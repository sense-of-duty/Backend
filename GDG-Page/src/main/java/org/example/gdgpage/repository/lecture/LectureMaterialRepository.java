package org.example.gdgpage.repository.lecture;

import org.example.gdgpage.domain.lecture.LectureMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {

    Page<LectureMaterial> findByDeletedFalseOrderByPublishedDateDescIdDesc(Pageable pageable);
    Page<LectureMaterial> findByDeletedFalseAndTitleContainingIgnoreCaseOrderByPublishedDateDescIdDesc(String keyword, Pageable pageable);
    Optional<LectureMaterial> findByIdAndDeletedFalse(Long id);
}
