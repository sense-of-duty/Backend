package org.example.gdgpage.service.freeboard;

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
public class FreeboardFileStorageImpl implements FreeboardFileStorage {

    private final S3Client s3Client;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp"
    );

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    @Value("${app.s3.freeboard-prefix:attachments/freeboard}")
    private String freeboardPrefix;

    @Value("${app.s3.public-base-url:}")
    private String publicBaseUrl;

    @Override
    public String uploadFreePostImage(Long userId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_ATTACHMENT_FILE);
        }

        if (!ALLOWED_CONTENT_TYPES.contains(
                file.getContentType() == null ? "" : file.getContentType().toLowerCase()
        )) {
            throw new BadRequestException(ErrorMessage.INVALID_ATTACHMENT_FILE);
        }

        String ext = extractExtension(file);
        String key = String.format("%s/%d/%s.%s",
                normalizePrefix(freeboardPrefix),
                userId,
                UUID.randomUUID(),
                ext
        );

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new BadRequestException(ErrorMessage.INVALID_ATTACHMENT_FILE);
        }

        return buildPublicUrl(key);
    }

    private String buildPublicUrl(String key) {
        String encoded = URLEncoder.encode(key, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            String base = publicBaseUrl.endsWith("/") ?
                    publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
            return base + "/" + encoded;
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                s3Client.serviceClientConfiguration().region().id(),
                encoded
        );
    }

    private String extractExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) {
            return name.substring(name.lastIndexOf('.') + 1);
        }
        return "png";
    }

    private String normalizePrefix(String prefix) {
        return prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
    }
}
