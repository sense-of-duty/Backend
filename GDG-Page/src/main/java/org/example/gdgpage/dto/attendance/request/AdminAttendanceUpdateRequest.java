package org.example.gdgpage.dto.attendance.request;

import jakarta.validation.constraints.NotNull;
import org.example.gdgpage.domain.attendance.AttendanceStatus;

public record AdminAttendanceUpdateRequest(
        @NotNull
        AttendanceStatus status
) {}
