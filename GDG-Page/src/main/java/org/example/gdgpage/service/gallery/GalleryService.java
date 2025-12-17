package org.example.gdgpage.service.gallery;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.gallery.Gallery;
import org.example.gdgpage.dto.gallery.response.GalleryResponse;
import org.example.gdgpage.dto.gallery.response.GallerySummaryResponse;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.mapper.gallery.GalleryMapper;
import org.example.gdgpage.repository.gallery.GalleryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

        GalleryFileStorage.StoredFile stored = galleryFileStorage.storeGalleryFile(file);
        gallery.attachImage(stored.fileUrl(), stored.fileKey(), stored.originalFileName(), stored.contentType(), stored.fileSize());
    }

    @Transactional
    public void deletePhoto(Long photoId) {
        Gallery gallery = galleryRepository.findByIdAndDeletedFalse(photoId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_PHOTO));

        gallery.softDelete();
    }
}
