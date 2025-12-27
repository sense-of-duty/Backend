package org.example.gdgpage.service.attendance;

import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.dto.attendance.response.WeekCreateResponse;

public interface AttendanceWeekService {
    WeekCreateResponse createNextWeek(PartType part);
}
