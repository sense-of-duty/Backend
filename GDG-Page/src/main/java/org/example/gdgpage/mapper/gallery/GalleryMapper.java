package org.example.gdgpage.mapper.gallery;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.gallery.Gallery;
import org.example.gdgpage.dto.gallery.response.GalleryResponse;
import org.example.gdgpage.dto.gallery.response.GallerySummaryResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GalleryMapper {

    public static GalleryResponse toGalleryResponse(Gallery gallery) {
        if (gallery == null) {
            return null;
        }

        return new GalleryResponse(
                gallery.getId(),
                gallery.getImageUrl(),
                gallery.getOriginalFileName(),
                gallery.getContentType(),
                gallery.getFileSize(),
                gallery.getCreatedAt(),
                gallery.getUpdatedAt()
        );
    }

    public static GallerySummaryResponse toGallerySummaryResponse(Gallery gallery) {
        if (gallery == null) {
            return null;
        }

        return new GallerySummaryResponse(
                gallery.getId(),
                gallery.getImageUrl(),
                gallery.getCreatedAt()
        );
    }
}

