package org.example.gdgpage.repository.attendance;

import org.example.gdgpage.domain.attendance.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
}
