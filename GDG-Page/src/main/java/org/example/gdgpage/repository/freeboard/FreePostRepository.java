package org.example.gdgpage.repository.freeboard;

import org.example.gdgpage.domain.freeboard.FreePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreePostRepository extends JpaRepository<FreePost, Long> {

    @Query("""
        select p from FreePost p
        join fetch p.author
        where (:keyword is null or :keyword = '' 
               or p.isPinned = true
               or lower(p.title) like lower(concat('%', :keyword, '%')))
        order by p.isPinned desc, p.createdAt desc
    """)
    List<FreePost> findAllWithAuthorAndKeyword(@Param("keyword") String keyword);
}
