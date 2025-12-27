package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyAttendanceSummaryResponse(
        Long weekId,
        int weekNo,
        String status,
        LocalDateTime checkedAt,
        LocalDateTime updatedAt
) {}
