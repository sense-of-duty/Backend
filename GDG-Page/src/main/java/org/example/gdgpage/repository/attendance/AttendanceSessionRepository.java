package org.example.gdgpage.repository.attendance;

import org.example.gdgpage.domain.attendance.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
}
