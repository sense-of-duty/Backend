package org.example.gdgpage.repository.freeboard;

import org.example.gdgpage.domain.freeboard.FreeComment;
import org.example.gdgpage.domain.freeboard.FreePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreeCommentRepository extends JpaRepository<FreeComment, Long> {
    List<FreeComment> findByPostOrderByCreatedAtAsc(FreePost post);
}
