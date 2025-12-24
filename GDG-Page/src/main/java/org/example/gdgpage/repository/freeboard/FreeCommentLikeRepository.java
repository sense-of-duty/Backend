package org.example.gdgpage.repository.freeboard;

import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.freeboard.FreeComment;
import org.example.gdgpage.domain.freeboard.FreeCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreeCommentLikeRepository extends JpaRepository<FreeCommentLike, Long> {

    boolean existsByUserAndComment(User user, FreeComment comment);
    Optional<FreeCommentLike> findByUserAndComment(User user, FreeComment comment);
}
