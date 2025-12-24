package org.example.gdgpage.domain.gallery;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.common.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "photos")
public class Gallery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "image_key", nullable = false, length = 600)
    private String imageKey;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "content_type", length = 80)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void attachImage(String imageUrl, String imageKey, String originalFileName, String contentType, Long fileSize) {
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public static Gallery create(String imageUrl, String imageKey,
                               String originalFileName, String contentType, Long fileSize) {
        Gallery gallery = new Gallery();
        gallery.imageUrl = imageUrl;
        gallery.imageKey = imageKey;
        gallery.originalFileName = originalFileName;
        gallery.contentType = contentType;
        gallery.fileSize = fileSize;
        gallery.deleted = false;
        return gallery;
    }
}
