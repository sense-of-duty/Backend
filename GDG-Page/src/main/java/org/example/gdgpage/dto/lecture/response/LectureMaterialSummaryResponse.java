package org.example.gdgpage.dto.lecture.response;

import java.time.LocalDate;

public record LectureMaterialSummaryResponse(
        Long id,
        String title,
        LocalDate publishedDate,
        String contentPreview,
        String fileUrl,
        boolean bookmarked
) {}
