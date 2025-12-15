package org.example.gdgpage.service.user;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageStorage {
    String storeProfileImage(Long userId, MultipartFile file);
}
