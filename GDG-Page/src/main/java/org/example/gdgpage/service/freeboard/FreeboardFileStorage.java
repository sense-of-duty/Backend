package org.example.gdgpage.service.freeboard;

import org.springframework.web.multipart.MultipartFile;

public interface FreeboardFileStorage {
    String uploadFreePostImage(Long userId, MultipartFile file);
}
