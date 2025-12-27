package org.example.gdgpage.service.freeboard;

import org.springframework.web.multipart.MultipartFile;

public interface FreeboardFileStorage {
    StoredFile uploadFreePostImage(Long userId, MultipartFile file);
    void deleteFreePostImage(String fileKey);

    record StoredFile(
            String fileUrl,
            String fileKey,
            String originalFileName,
            String contentType,
            Long fileSize
    ) {}
}
