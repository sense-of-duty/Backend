package org.example.gdgpage.service.assignment;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentStorage {
    String uploadAssignmentAttachment(Long userId, MultipartFile file);
    String uploadSubmissionAttachment(Long userId, MultipartFile file);
}
