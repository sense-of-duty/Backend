package org.example.gdgpage.repository.attendance;

import org.example.gdgpage.domain.attendance.AttendanceSession;
import org.example.gdgpage.domain.auth.PartType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    @Query("""
        select s from AttendanceSession s
        where s.week.id = :weekId
          and s.status = 'OPEN'
          and s.closedAt is null
          and s.expiresAt > :now
    """)
    Optional<AttendanceSession> findActiveByWeekId(@Param("weekId") Long weekId, @Param("now") LocalDateTime now);

    @Query("""
        select s from AttendanceSession s
        join s.week w
        where w.part = :part
          and s.status = 'OPEN'
          and s.closedAt is null
          and s.expiresAt > :now
        order by s.openedAt desc
    """)
    Optional<AttendanceSession> findActiveByPart(@Param("part") PartType part, @Param("now") LocalDateTime now);
}
