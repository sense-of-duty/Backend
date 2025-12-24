package org.example.gdgpage.dto.gallery.response;

import java.time.LocalDateTime;

public record GallerySummaryResponse(
        Long id,
        String imageUrl,
        LocalDateTime createdAt
) {}
