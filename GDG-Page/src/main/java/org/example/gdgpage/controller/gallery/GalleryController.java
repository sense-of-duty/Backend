package org.example.gdgpage.controller.gallery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.dto.gallery.response.GalleryResponse;
import org.example.gdgpage.dto.gallery.response.GallerySummaryResponse;
import org.example.gdgpage.service.gallery.GalleryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photos")
public class GalleryController {

    private final GalleryService galleryService;

    @Operation(summary = "사진 목록 조회(공개)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<GallerySummaryResponse>> photoList(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(galleryService.photoList(page, size));
    }

    @Operation(summary = "사진 단건 조회(공개)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사진 없음")
    })
    @GetMapping("/{photoId}")
    public ResponseEntity<GalleryResponse> getPhoto(@PathVariable Long photoId) {
        return ResponseEntity.ok(galleryService.getPhoto(photoId));
    }
}

