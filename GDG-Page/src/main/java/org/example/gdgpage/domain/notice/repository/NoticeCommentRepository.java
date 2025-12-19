package org.example.gdgpage.domain.notice.repository;

import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.domain.notice.entity.NoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {

    List<NoticeComment> findByNoticeOrderByCreatedAtAsc(Notice notice);

    List<NoticeComment> findByNoticeAndParentIsNullOrderByCreatedAtAsc(Notice notice);

    List<NoticeComment> findByAuthorId(Long authorId);
}