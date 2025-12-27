package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

import java.time.LocalDateTime;

@Builder
public record ActiveSessionResponse(
        Long weekId,
        PartType part,
        int weekNo,
        LocalDateTime expiresAt
) {}

