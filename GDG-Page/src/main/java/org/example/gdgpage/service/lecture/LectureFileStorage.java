package org.example.gdgpage.service.lecture;

import org.springframework.web.multipart.MultipartFile;

public interface LectureFileStorage {
    StoredFile storeLectureFile(Long lectureId, MultipartFile file);
    void deleteLectureFile(String fileKey);

    record StoredFile(
            String fileUrl,
            String fileKey,
            String originalFileName,
            String contentType,
            Long fileSize
    ) {}
}
