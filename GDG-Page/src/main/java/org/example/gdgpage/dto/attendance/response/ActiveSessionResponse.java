package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ActiveSessionResponse(
        Long weekId,
        int weekNo,
        LocalDateTime expiresAt
) {}

