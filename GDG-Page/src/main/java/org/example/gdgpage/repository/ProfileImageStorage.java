package org.example.gdgpage.repository;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageStorage {
    String storeProfileImage(Long userId, MultipartFile file);
}
