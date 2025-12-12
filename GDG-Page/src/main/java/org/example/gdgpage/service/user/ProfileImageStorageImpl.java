package org.example.gdgpage.service.user;

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
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProfileImageStorageImpl implements ProfileImageStorage {

    private final S3Client s3Client;

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    @Value("${app.s3.profile-prefix}")
    private String profilePrefix;

    @Value("${app.s3.public-base-url:}")
    private String publicBaseUrl;

    @Override
    public String storeProfileImage(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_PROFILE_IMAGE);
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException(ErrorMessage.INVALID_PROFILE_IMAGE);
        }

        String originalFilename = file.getOriginalFilename();
        String extracted = extractExtension(originalFilename, contentType);

        String key = String.format("%s/%d/%s.%s",
                normalizePrefix(profilePrefix),
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
            throw new BadRequestException(ErrorMessage.INVALID_PROFILE_IMAGE);
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
        }

        return "img";
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "profiles";
        }
        return prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
    }
}
