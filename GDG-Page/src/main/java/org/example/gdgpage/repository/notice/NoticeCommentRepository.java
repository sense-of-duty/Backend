package org.example.gdgpage.repository.notice;

import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.domain.notice.entity.NoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {

    @Query("""
    select c from NoticeComment c
    join fetch c.author
    left join fetch c.parent
    where c.notice = :notice
    order by c.createdAt asc
""")
    List<NoticeComment> findAllByNoticeOrderByCreatedAtAsc(@Param("notice") Notice notice);
}
