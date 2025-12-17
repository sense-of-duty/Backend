package org.example.gdgpage.repository.freeboard;

import org.example.gdgpage.domain.freeboard.FreeComment;
import org.example.gdgpage.domain.freeboard.FreePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreeCommentRepository extends JpaRepository<FreeComment, Long> {

    @Query("""
        select c from FreeComment c
        join fetch c.author
        left join fetch c.parent
        where c.post = :post
        order by c.createdAt asc
    """)
    List<FreeComment> findByPostWithAuthorAndParent(@Param("post") FreePost post);
}
