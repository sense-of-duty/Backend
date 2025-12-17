package org.example.gdgpage.repository.gallery;

import org.example.gdgpage.domain.gallery.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    Page<Gallery> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    Optional<Gallery> findByIdAndDeletedFalse(Long id);
}
