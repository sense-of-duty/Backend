package org.example.gdgpage.controller.gallery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.dto.gallery.response.GalleryResponse;
import org.example.gdgpage.service.gallery.GalleryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/photos")
public class AdminGalleryController {

    private final GalleryService galleryService;

    @Operation(summary = "사진 업로드(관리자)", description = "multipart/form-data로 file 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "파일 유효하지 않음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<GalleryResponse> uploadPhoto(@RequestPart("file") MultipartFile file) {
        GalleryResponse created = galleryService.uploadPhoto(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "사진 교체(관리자)", description = "기존 사진을 새 파일로 교체")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "파일 유효하지 않음"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사진 없음")
    })
    @PatchMapping(value = "/{photoId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> updatePhoto(@PathVariable Long photoId,
                                            @RequestPart("file") MultipartFile file) {
        galleryService.updatePhoto(photoId, file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사진 삭제(관리자)", description = "소프트 삭제 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사진 없음")
    })
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long photoId) {
        galleryService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}

