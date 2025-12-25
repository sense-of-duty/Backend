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

    @Query("SELECT c FROM NoticeComment c JOIN FETCH c.author WHERE c.notice = :notice ORDER BY c.createdAt ASC")
    List<NoticeComment> findAllByNoticeOrderByCreatedAtAsc(@Param("notice") Notice notice);
}