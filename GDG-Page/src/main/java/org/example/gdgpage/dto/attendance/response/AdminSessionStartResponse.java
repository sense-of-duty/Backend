package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdminSessionStartResponse(
        Long sessionId,
        Long weekId,
        int weekNo,
        String code,          // 관리자에게만 반환
        LocalDateTime expiresAt
) {}
