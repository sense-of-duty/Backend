package org.example.gdgpage.dto.lecture.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LectureMaterialResponse(
        Long id,
        String title,
        LocalDate publishedDate,
        String content,
        String fileUrl,
        String originalFileName,
        String contentType,
        Long fileSize,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean bookmarked
) {}
