package org.example.gdgpage.service.attendance;

import org.example.gdgpage.domain.attendance.AttendanceStatus;
import org.example.gdgpage.dto.attendance.response.ActiveSessionResponse;
import org.example.gdgpage.dto.attendance.response.AttendanceCheckResponse;
import org.example.gdgpage.dto.attendance.response.MyAttendanceResponse;

public interface AttendanceService {
    AttendanceCheckResponse checkAttendance(Long userId, String code);
    MyAttendanceResponse getMyAttendance(Long userId);
    void adminUpdateStatus(Long adminId, Long weekId, Long targetUserId, AttendanceStatus status);
    ActiveSessionResponse getActive(Long userId);
}
