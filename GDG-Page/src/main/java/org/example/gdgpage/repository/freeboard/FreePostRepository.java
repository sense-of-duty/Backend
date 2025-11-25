package org.example.gdgpage.repository.freeboard;

import org.example.gdgpage.domain.freeboard.FreePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreePostRepository extends JpaRepository<FreePost, Long> {
    List<FreePost> findByTitleContaining(String keyword);
}
