package org.example.gdgpage.mapper.lecture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.lecture.LectureMaterial;
import org.example.gdgpage.dto.lecture.response.LectureMaterialResponse;
import org.example.gdgpage.dto.lecture.response.LectureMaterialSummaryResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LectureMaterialMapper {

    public static LectureMaterialResponse toLectureMaterialResponse(LectureMaterial material, boolean bookmarked) {
        if (material == null) {
            return null;
        }

        return new LectureMaterialResponse(
                material.getId(),
                material.getTitle(),
                material.getPublishedDate(),
                material.getContent(),
                material.getFileUrl(),
                material.getOriginalFileName(),
                material.getContentType(),
                material.getFileSize(),
                material.getCreatedAt(),
                material.getUpdatedAt(),
                bookmarked
        );
    }

    public static LectureMaterialSummaryResponse toLectureMaterialSummaryResponse(LectureMaterial material, boolean bookmarked) {
        if (material == null) {
            return null;
        }

        return new LectureMaterialSummaryResponse(
                material.getId(),
                material.getTitle(),
                material.getPublishedDate(),
                preview(material.getContent()),
                material.getFileUrl(),
                bookmarked
        );
    }

    private static String preview(String content) {
        if (content == null) {
            return null;
        }

        return content.length() <= 100 ? content : content.substring(0, 100);
    }
}

