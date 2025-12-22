package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

import java.time.LocalDateTime;

@Builder
public record AttendanceCheckResponse(
        Long weekId,
        PartType part,
        int weekNo,
        String status,
        LocalDateTime checkedAt
) {}
