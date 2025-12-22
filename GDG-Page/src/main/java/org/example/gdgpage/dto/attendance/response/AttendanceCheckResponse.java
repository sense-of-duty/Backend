package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AttendanceCheckResponse(
        Long weekId,
        int weekNo,
        String status, // "PRESENT"
        LocalDateTime checkedAt
) {}
