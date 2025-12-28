package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

import java.util.List;

@Builder
public record MyAttendanceResponse(
        PartType part,
        List<MyAttendanceSummaryResponse> weeks
) {}
