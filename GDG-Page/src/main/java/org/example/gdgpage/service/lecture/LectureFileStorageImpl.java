package org.example.gdgpage.service.lecture;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LectureFileStorageImpl implements LectureFileStorage {

    private static final long MAX_SIZE = 20L * 1024 * 1024;

    private final S3Client s3Client;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/zip",
            "image/png",
            "image/jpeg",
            "image/webp"
    );

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    @Value("${app.s3.lecture-prefix:lectures}")
    private String lecturePrefix;

    @Value("${app.s3.public-base-url:}")
    private String publicBaseUrl;

    @Override
    public StoredFile storeLectureFile(Long lectureId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_LECTURE_FILE);
        }

        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException(ErrorMessage.INVALID_LECTURE_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(ErrorMessage.INVALID_LECTURE_FILE);
        }

        String originalFileName = file.getOriginalFilename();
        String ext = extractExtension(originalFileName, contentType);

        String key = String.format("%s/%d/%s.%s",
                normalizePrefix(lecturePrefix),
                lectureId,
                UUID.randomUUID(),
                ext
        );

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new BadRequestException(ErrorMessage.INVALID_LECTURE_FILE);
        }

        String url = buildPublicUrl(key);

        return new StoredFile(
                url,
                key,
                originalFileName,
                contentType,
                file.getSize()
        );
    }

    @Override
    public void deleteLectureFile(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (Exception ignore) {
        }
    }

    private String buildPublicUrl(String key) {
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
            return base + "/" + encodedKey;
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                s3Client.serviceClientConfiguration().region().id(),
                encodedKey
        );
    }

    private String extractExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }

        if ("application/pdf".equalsIgnoreCase(contentType)) {
            return "pdf";
        }
        if (contentType != null && contentType.toLowerCase().contains("presentation")) {
            return "pptx";
        }
        if (contentType != null && contentType.toLowerCase().contains("wordprocessingml")) {
            return "docx";
        }
        if (contentType != null && contentType.toLowerCase().contains("spreadsheetml")) {
            return "xlsx";
        }
        if ("application/zip".equalsIgnoreCase(contentType)) {
            return "zip";
        }
        if ("image/png".equalsIgnoreCase(contentType)) {
            return "png";
        }
        if ("image/jpeg".equalsIgnoreCase(contentType)) {
            return "jpg";
        }
        if ("image/webp".equalsIgnoreCase(contentType)) {
            return "webp";
        }

        return "bin";
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "lectures";
        }
        return prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
    }
}
