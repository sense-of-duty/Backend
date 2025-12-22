package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

import java.time.LocalDateTime;

@Builder
public record AdminSessionStartResponse(
        Long sessionId,
        Long weekId,
        PartType part,
        int weekNo,
        String code,              // 관리자만
        LocalDateTime expiresAt
) {}
