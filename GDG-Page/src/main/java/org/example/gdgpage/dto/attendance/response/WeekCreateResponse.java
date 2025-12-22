package org.example.gdgpage.dto.attendance.response;

import lombok.Builder;

@Builder
public record WeekCreateResponse(
        Long weekId,
        Long courseId,
        int weekNo
) {}
