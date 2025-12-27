package org.example.gdgpage.service.assignment;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttachmentStorageImpl implements AttachmentStorage {

    private static final long maxSize = 20 * 1024 * 1024;

    private final S3Client s3Client;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp",
            "application/pdf"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "png",
            "jpg",
            "jpeg",
            "webp",
            "pdf"
    );

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    @Value("${app.s3.assignment-attachment-prefix:attachments/assignments}")
    private String assignmentAttachmentPrefix;

    @Value("${app.s3.submission-attachment-prefix:attachments/submissions}")
    private String submissionAttachmentPrefix;

    @Value("${app.s3.public-base-url:}")
    private String publicBaseUrl;

    @Override
    public String uploadAssignmentAttachment(Long userId, MultipartFile file) {
        return upload(userId, file, assignmentAttachmentPrefix);
    }

    @Override
    public String uploadSubmissionAttachment(Long userId, MultipartFile file) {
        return upload(userId, file, submissionAttachmentPrefix);
    }

    private String upload(Long userId, MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_ATTACHMENT_FILE);
        }

        if (file.getSize() > maxSize) {
            throw new BadRequestException(ErrorMessage.INVALID_ATTACHMENT_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(ErrorMessage.INVALID_ATTACHMENT_FILE);
        }

        String originalFilename = file.getOriginalFilename();
        String extracted = extractExtension(originalFilename, contentType);

        if (!ALLOWED_EXTENSIONS.contains(extracted.toLowerCase())) {
            throw new BadRequestException(ErrorMessage.INVALID_ATTACHMENT_FILE);
        }

        String key = String.format("%s/%d/%s.%s",
                normalizePrefix(prefix),
                userId,
                UUID.randomUUID(),
                extracted
        );

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new BadRequestException(ErrorMessage.INVALID_ATTACHMENT_FILE);
        }

        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
            return publicBaseUrl + "/" + encodedKey.replaceAll("\\+", "%20");
        } else {
            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName,
                    s3Client.serviceClientConfiguration().region().id(),
                    key
            );
        }
    }

    private String extractExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }

        if ("image/jpeg".equalsIgnoreCase(contentType)) {
            return "jpg";
        } else if ("image/png".equalsIgnoreCase(contentType)) {
            return "png";
        } else if ("image/webp".equalsIgnoreCase(contentType)) {
            return "webp";
        } else if ("application/pdf".equalsIgnoreCase(contentType)) {
            return "pdf";
        }

        return "file";
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "attachments";
        }
        return prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
    }
}

