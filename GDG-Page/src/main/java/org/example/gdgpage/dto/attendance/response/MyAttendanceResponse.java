package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MyAttendanceResponse(
        Long courseId,
        List<MyAttendanceSummaryResponse> weeks
) {}
