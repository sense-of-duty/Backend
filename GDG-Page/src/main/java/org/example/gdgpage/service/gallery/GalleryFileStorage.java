package org.example.gdgpage.service.gallery;

import org.springframework.web.multipart.MultipartFile;

public interface GalleryFileStorage {
    StoredFile storeGalleryFile(MultipartFile file);

    void deleteGalleryFile(String fileKey);

    record StoredFile(
            String fileUrl,
            String fileKey,
            String originalFileName,
            String contentType,
            Long fileSize
    ) {}
}
