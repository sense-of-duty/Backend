package org.example.gdgpage.repository.freeboard;

import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.freeboard.FreePost;
import org.example.gdgpage.domain.freeboard.FreePostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreePostLikeRepository extends JpaRepository<FreePostLike, Long> {

    boolean existsByUserAndPost(User user, FreePost post);

    Optional<FreePostLike> findByUserAndPost(User user, FreePost post);
}
