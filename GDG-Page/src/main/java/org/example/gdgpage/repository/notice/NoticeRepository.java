package org.example.gdgpage.repository.notice;

import org.example.gdgpage.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByDeletedAtIsNullOrderByIsPinnedDescCreatedAtDesc();

    Optional<Notice> findByIdAndDeletedAtIsNull(Long id);

    @Modifying
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    void updateViewCount(@Param("id") Long id);
}