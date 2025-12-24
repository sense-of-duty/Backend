package org.example.gdgpage.controller.lecture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.lecture.response.LectureMaterialResponse;
import org.example.gdgpage.dto.lecture.response.LectureMaterialSummaryResponse;
import org.example.gdgpage.service.lecture.LectureMaterialService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lectures")
public class LectureMaterialController {

    private final LectureMaterialService lectureMaterialService;

    @Operation(summary = "강의자료 목록 조회", description = "키워드(제목) 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping
    public ResponseEntity<List<LectureMaterialSummaryResponse>> list(@AuthenticationPrincipal AuthUser authUser,
                                                                     @RequestParam(required = false) String keyword,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(lectureMaterialService.lectureMaterialList(authUser.id(), keyword, page, size));
    }

    @Operation(summary = "강의자료 상세 조회", description = "강의자료 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "강의자료 없음")
    })
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureMaterialResponse> get(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureMaterialService.getLectureMaterial(authUser.id(), lectureId));
    }

    @Operation(summary = "즐겨찾기 토글", description = "현재 사용자의 즐겨찾기 상태를 토글")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 성공"),
            @ApiResponse(responseCode = "404", description = "강의자료 없음")
    })
    @PostMapping("/{lectureId}/bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(@AuthenticationPrincipal AuthUser authUser,
                                                              @PathVariable Long lectureId) {
        boolean bookmarked = lectureMaterialService.toggleBookmark(authUser.id(), lectureId);
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }

    @Operation(summary = "내 즐겨찾기 목록", description = "내가 즐겨찾기한 강의자료 ID 목록")
    @GetMapping("/bookmarks/myBookmarks")
    public ResponseEntity<List<Long>> myBookmarks(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(lectureMaterialService.myBookmarks(authUser.id()));
    }
}
