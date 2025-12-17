package org.example.gdgpage.repository.lecture;

import org.example.gdgpage.domain.lecture.LectureBookmark;
import org.example.gdgpage.domain.lecture.LectureMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureBookmarkRepository extends JpaRepository<LectureBookmark, Long> {

    Optional<LectureBookmark> findByUserIdAndLecture(Long userId, LectureMaterial lecture);
    boolean existsByUserIdAndLecture_Id(Long userId, Long lectureId);

    @Query("""
        select b.lecture.id
        from LectureBookmark b
        where b.userId = :userId
          and b.lecture.id in :lectureIds
          and b.lecture.deleted = false
    """)
    List<Long> findBookmarkedLectureIds(@Param("userId") Long userId,
                                        @Param("lectureIds") List<Long> lectureIds);

    @Query("""
    select b.lecture.id
    from LectureBookmark b
    where b.userId = :userId
      and b.lecture.deleted = false
""")
    List<Long> findAllActiveLectureIdsByUserId(@Param("userId") Long userId);
}
