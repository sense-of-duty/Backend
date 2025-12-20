package org.example.gdgpage.repository.freeboard;

import org.example.gdgpage.domain.freeboard.FreePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FreePostRepository extends JpaRepository<FreePost, Long> {

    @Query("""
        select p from FreePost p
        join fetch p.author
        order by p.createdAt desc, p.createdAt desc
    """)
    List<FreePost> findAllWithAuthor();
}
