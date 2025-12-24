package org.example.gdgpage.service.gallery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gdgpage.domain.gallery.Gallery;
import org.example.gdgpage.dto.gallery.response.GalleryResponse;
import org.example.gdgpage.dto.gallery.response.GallerySummaryResponse;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.exception.StorageException;
import org.example.gdgpage.mapper.gallery.GalleryMapper;
import org.example.gdgpage.repository.gallery.GalleryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final GalleryFileStorage galleryFileStorage;

    @Transactional(readOnly = true)
    public List<GallerySummaryResponse> photoList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return galleryRepository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(GalleryMapper::toGallerySummaryResponse)
                .getContent();
    }

    @Transactional(readOnly = true)
    public GalleryResponse getPhoto(Long photoId) {
        Gallery gallery = galleryRepository.findByIdAndDeletedFalse(photoId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_PHOTO));

        return GalleryMapper.toGalleryResponse(gallery);
    }

    @Transactional
    public GalleryResponse uploadPhoto(MultipartFile file) {
        GalleryFileStorage.StoredFile stored = galleryFileStorage.storeGalleryFile(file);

        runAfterRollback(() -> safeDelete(stored.fileKey()));

        Gallery gallery = galleryRepository.save(
                Gallery.create(
                        stored.fileUrl(),
                        stored.fileKey(),
                        stored.originalFileName(),
                        stored.contentType(),
                        stored.fileSize()
                )
        );

        return GalleryMapper.toGalleryResponse(gallery);
    }

    @Transactional
    public void updatePhoto(Long photoId, MultipartFile file) {
        Gallery gallery = galleryRepository.findByIdAndDeletedFalse(photoId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_PHOTO));

        String oldKey = gallery.getImageKey();

        GalleryFileStorage.StoredFile stored = galleryFileStorage.storeGalleryFile(file);

        runAfterRollback(() -> safeDelete(stored.fileKey()));

        gallery.attachImage(
                stored.fileUrl(),
                stored.fileKey(),
                stored.originalFileName(),
                stored.contentType(),
                stored.fileSize()
        );

        runAfterCommit(() -> safeDelete(oldKey));
    }

    @Transactional
    public void deletePhoto(Long photoId) {
        Gallery gallery = galleryRepository.findByIdAndDeletedFalse(photoId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_PHOTO));

        String key = gallery.getImageKey();
        gallery.softDelete();

        runAfterCommit(() -> safeDelete(key));
    }

    private void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }

    private void runAfterRollback(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    action.run();
                }
            }
        });
    }

    private void safeDelete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        try {
            galleryFileStorage.deleteGalleryFile(key);
        } catch (StorageException e) {
            log.warn("S3 delete failed. key={}", key, e);
        } catch (Exception e) {
            log.warn("Unexpected error while deleting S3 object. key={}", key, e);
        }
    }
}
