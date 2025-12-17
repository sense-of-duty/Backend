package org.example.gdgpage.dto.gallery.response;

import java.time.LocalDateTime;

public record GalleryResponse(
        Long id,
        String imageUrl,
        String originalFileName,
        String contentType,
        Long fileSize,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
