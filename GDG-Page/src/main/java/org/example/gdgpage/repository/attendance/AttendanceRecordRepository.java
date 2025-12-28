package org.example.gdgpage.repository.attendance;

import org.example.gdgpage.domain.attendance.AttendanceRecord;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByWeekAndUser(Week week, User user);

    @Query("""
        select r from AttendanceRecord r
        where r.week.id in :weekIds
          and r.user.id = :userId
    """)
    List<AttendanceRecord> findAllByWeekIdsAndUserId(@Param("weekIds") List<Long> weekIds, @Param("userId") Long userId);
}
