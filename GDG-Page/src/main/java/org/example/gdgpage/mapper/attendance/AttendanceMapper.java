package org.example.gdgpage.mapper.attendance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.attendance.AttendanceRecord;
import org.example.gdgpage.domain.attendance.AttendanceSession;
import org.example.gdgpage.domain.attendance.AttendanceStatus;
import org.example.gdgpage.domain.attendance.Week;
import org.example.gdgpage.dto.attendance.response.ActiveSessionResponse;
import org.example.gdgpage.dto.attendance.response.AdminSessionStartResponse;
import org.example.gdgpage.dto.attendance.response.AttendanceCheckResponse;
import org.example.gdgpage.dto.attendance.response.MyAttendanceSummaryResponse;
import org.example.gdgpage.dto.attendance.response.WeekCreateResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AttendanceMapper {

    public static WeekCreateResponse toWeekCreateResponse(Week week) {
        return WeekCreateResponse.builder()
                .weekId(week.getId())
                .part(week.getPart())
                .weekNo(week.getWeekNo())
                .build();
    }

    public static AdminSessionStartResponse toAdminSessionStartResponse(AttendanceSession session, String rawCode) {
        Week week = session.getWeek();
        return AdminSessionStartResponse.builder()
                .sessionId(session.getId())
                .weekId(week.getId())
                .part(week.getPart())
                .weekNo(week.getWeekNo())
                .code(rawCode)
                .expiresAt(session.getExpiresAt())
                .build();
    }

    public static ActiveSessionResponse toActiveSessionResponse(AttendanceSession session) {
        Week week = session.getWeek();
        return ActiveSessionResponse.builder()
                .weekId(week.getId())
                .part(week.getPart())
                .weekNo(week.getWeekNo())
                .expiresAt(session.getExpiresAt())
                .build();
    }

    public static AttendanceCheckResponse toAttendanceCheckResponse(AttendanceRecord record) {
        Week week = record.getWeek();
        return AttendanceCheckResponse.builder()
                .weekId(week.getId())
                .part(week.getPart())
                .weekNo(week.getWeekNo())
                .status(record.getStatus().name())
                .checkedAt(record.getCheckedAt())
                .build();
    }

    public static MyAttendanceSummaryResponse toMySummary(Week week, AttendanceRecord recordOrNull) {
        if (recordOrNull == null) {
            return MyAttendanceSummaryResponse.builder()
                    .weekId(week.getId())
                    .weekNo(week.getWeekNo())
                    .status(AttendanceStatus.ABSENT.name())
                    .checkedAt(null)
                    .updatedAt(null)
                    .build();
        }

        return MyAttendanceSummaryResponse.builder()
                .weekId(week.getId())
                .weekNo(week.getWeekNo())
                .status(recordOrNull.getStatus().name())
                .checkedAt(recordOrNull.getCheckedAt())
                .updatedAt(recordOrNull.getUpdatedAt())
                .build();
    }
}
