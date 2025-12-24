package org.example.gdgpage.service.gallery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GalleryFileStorageImpl implements GalleryFileStorage {

    private static final long MAX_SIZE = 10L * 1024 * 1024; // 10MB

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp"
    );

    private final S3Client s3Client;

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    @Value("${app.s3.photo-prefix:photos}")
    private String photoPrefix;

    @Value("${app.s3.public-base-url:}")
    private String publicBaseUrl;

    @Override
    public StoredFile storeGalleryFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_PHOTO_FILE);
        }

        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException(ErrorMessage.INVALID_PHOTO_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(ErrorMessage.INVALID_PHOTO_FILE);
        }

        String originalFileName = file.getOriginalFilename();

        String ext = extensionFromContentType(contentType);

        String key = String.format("%s/%s.%s",
                normalizePrefix(photoPrefix),
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
            throw new BadRequestException(ErrorMessage.INVALID_PHOTO_FILE);
        } catch (Exception e) {
            throw new StorageException("Photo upload to S3 failed", e);
        }

        return new StoredFile(
                buildPublicUrl(key),
                key,
                originalFileName,
                contentType,
                file.getSize()
        );
    }

    @Override
    public void deleteGalleryFile(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) return;

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new StorageException("Photo delete from S3 failed. key=" + fileKey, e);
        }
    }

    private String buildPublicUrl(String key) {
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            return publicBaseUrl.endsWith("/") ? publicBaseUrl + key : publicBaseUrl + "/" + key;
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                s3Client.serviceClientConfiguration().region().id(),
                key
        );
    }

    private String extensionFromContentType(String contentType) {
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
        if (prefix == null || prefix.isBlank()) return "photos";
        return prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
    }
}
