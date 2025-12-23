package org.example.gdgpage.repository.notice;

import org.example.gdgpage.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByDeletedAtIsNullOrderByIsPinnedDescCreatedAtDesc();

}