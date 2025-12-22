package org.example.gdgpage.service.attendance;

import org.example.gdgpage.domain.attendance.AttendanceSession;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.dto.attendance.response.ActiveSessionResponse;
import org.example.gdgpage.dto.attendance.response.AdminSessionStartResponse;

public interface AttendanceSessionService {
    AdminSessionStartResponse startSession(Long weekId, Long adminId);
    void closeSession(Long sessionId);
    ActiveSessionResponse getActiveSessionForPart(PartType part);
    AttendanceSession getActiveSessionEntityForPartOrNull(PartType part);
    boolean matchesCode(AttendanceSession session, String rawCode);
    AttendanceSession getSession(Long sessionId);
}
