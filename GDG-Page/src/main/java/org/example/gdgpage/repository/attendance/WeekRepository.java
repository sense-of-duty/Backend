package org.example.gdgpage.repository.attendance;

import jakarta.persistence.LockModeType;
import org.example.gdgpage.domain.attendance.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeekRepository extends JpaRepository<Week, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Week w where w.courseId = :courseId order by w.weekNo desc")
    List<Week> findLatestForUpdate(@Param("courseId") Long courseId);

    List<Week> findAllByCourseIdOrderByWeekNoAsc(Long courseId);
}
