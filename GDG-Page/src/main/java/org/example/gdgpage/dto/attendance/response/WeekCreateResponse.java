package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

@Builder
public record WeekCreateResponse(
        Long weekId,
        PartType part,
        int weekNo
) {}
