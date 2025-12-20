package org.example.gdgpage.service.lecture;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.lecture.LectureBookmark;
import org.example.gdgpage.domain.lecture.LectureMaterial;
import org.example.gdgpage.dto.lecture.request.CreateLectureMaterialRequest;
import org.example.gdgpage.dto.lecture.request.UpdateLectureMaterialRequest;
import org.example.gdgpage.dto.lecture.response.LectureMaterialResponse;
import org.example.gdgpage.dto.lecture.response.LectureMaterialSummaryResponse;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.mapper.lecture.LectureMaterialMapper;
import org.example.gdgpage.repository.lecture.LectureBookmarkRepository;
import org.example.gdgpage.repository.lecture.LectureMaterialRepository;
import org.example.gdgpage.util.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LectureMaterialService {

    private final LectureMaterialRepository lectureMaterialRepository;
    private final LectureBookmarkRepository lectureBookmarkRepository;
    private final LectureFileStorage lectureFileStorage;

    @Transactional
    public LectureMaterialResponse create(CreateLectureMaterialRequest request, MultipartFile file) {
        LocalDate publishedDate = request.publishedDate() != null ? request.publishedDate() : LocalDate.now();

        LectureMaterial material = lectureMaterialRepository.save(
                LectureMaterial.create(request.title(), publishedDate, request.content())
        );

        if (file != null && !file.isEmpty()) {
            LectureFileStorage.StoredFile stored = lectureFileStorage.storeLectureFile(material.getId(), file);
            material.attachFile(stored.fileUrl(), stored.fileKey(), stored.originalFileName(), stored.contentType(), stored.fileSize());
        }

        return LectureMaterialMapper.toLectureMaterialResponse(material, false);
    }

    @Transactional(readOnly = true)
    public List<LectureMaterialSummaryResponse> lectureMaterialList(Long userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<LectureMaterial> pageResult = (keyword == null || keyword.isBlank()
                ? lectureMaterialRepository.findByDeletedFalseOrderByPublishedDateDescIdDesc(pageable)
                : lectureMaterialRepository.findByDeletedFalseAndTitleContainingIgnoreCaseOrderByPublishedDateDescIdDesc(keyword, pageable));

        List<LectureMaterial> materials = pageResult.getContent();

        List<Long> lectureIds = materials.stream()
                .map(LectureMaterial::getId)
                .toList();

        Set<Long> bookmarkedIdSet = lectureIds.isEmpty()
                ? java.util.Collections.emptySet()
                : new HashSet<>(lectureBookmarkRepository.findBookmarkedLectureIds(userId, lectureIds));

        return materials.stream()
                .map(material -> LectureMaterialMapper.toLectureMaterialSummaryResponse(
                        material,
                        bookmarkedIdSet.contains(material.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public LectureMaterialResponse getLectureMaterial(Long userId, Long lectureId) {
        LectureMaterial material = lectureMaterialRepository.findByIdAndDeletedFalse(lectureId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_LECTURE_MATERIAL));

        boolean bookmarked = lectureBookmarkRepository.existsByUserIdAndLecture_Id(userId, lectureId);

        return LectureMaterialMapper.toLectureMaterialResponse(material, bookmarked);
    }

    @Transactional
    public void updateMaterial(Long lectureId, UpdateLectureMaterialRequest request, MultipartFile file) {
        LectureMaterial material = lectureMaterialRepository.findByIdAndDeletedFalse(lectureId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_LECTURE_MATERIAL));

        material.update(request.title(), request.publishedDate(), request.content());

        if (file != null && !file.isEmpty()) {
            String oldKey = material.getFileKey();

            LectureFileStorage.StoredFile stored = lectureFileStorage.storeLectureFile(material.getId(), file);
            material.attachFile(stored.fileUrl(), stored.fileKey(), stored.originalFileName(), stored.contentType(), stored.fileSize());

            lectureFileStorage.deleteLectureFile(oldKey);
        }
    }

    @Transactional
    public void deleteMaterial(Long lectureId) {
        LectureMaterial material = lectureMaterialRepository.findByIdAndDeletedFalse(lectureId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_LECTURE_MATERIAL));

        material.softDelete();
    }

    @Transactional
    public boolean toggleBookmark(Long userId, Long lectureId) {
        LectureMaterial lecture = lectureMaterialRepository.findByIdAndDeletedFalse(lectureId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_LECTURE_MATERIAL));

        return lectureBookmarkRepository.findByUserIdAndLecture(userId, lecture)
                .map(existing -> {
                    lectureBookmarkRepository.delete(existing);
                    return false;
                })
                .orElseGet(() -> {
                    lectureBookmarkRepository.save(LectureBookmark.create(userId, lecture));
                    return true;
                });
    }

    @Transactional(readOnly = true)
    public List<Long> myBookmarks() {
        Long userId = AuthUtil.currentUserId();
        return lectureBookmarkRepository.findAllActiveLectureIdsByUserId(userId);
    }
}
