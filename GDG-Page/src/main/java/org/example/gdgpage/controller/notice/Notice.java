package org.example.gdgpage.controller.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.PrincipalDetails;
import org.example.gdgpage.dto.notice.request.post.NoticeCreateRequest;
import org.example.gdgpage.dto.notice.response.NoticeListResponse;
import org.example.gdgpage.dto.notice.response.post.NoticeResponse;
import org.example.gdgpage.service.notice.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class Notice { // 1. 클래스 이름을 파일명과 일치시킵니다.

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<?> createNotice(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody NoticeCreateRequest request
    ) {
        // 2. 로그인 체크 추가: 여기서 null 에러를 방지합니다.
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        Long userId = principalDetails.getUser().getId();
        Long noticeId = noticeService.createNotice(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeId);
    }

    @GetMapping
    public ResponseEntity<List<NoticeListResponse>> getAllNotices() {
        List<NoticeListResponse> response = noticeService.getAllNotices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long noticeId) {
        NoticeResponse response = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<?> updateNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody NoticeCreateRequest request
    ) {
        // 3. 수정 권한 확인 전 로그인 체크
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        Long userId = principalDetails.getUser().getId();
        noticeService.updateNotice(noticeId, userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        // 4. 삭제 권한 확인 전 로그인 체크
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        Long userId = principalDetails.getUser().getId();
        noticeService.deleteNotice(noticeId, userId);
        return ResponseEntity.noContent().build();
    }
}