package org.example.gdgpage.controller.notice;

import jakarta.persistence.EntityListeners;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.notice.request.post.NoticeCreateRequest;
import org.example.gdgpage.dto.notice.response.NoticeListResponse;
import org.example.gdgpage.dto.notice.response.post.NoticeResponse;
import org.example.gdgpage.service.notice.NoticeService;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
@EntityListeners(AuditingEntityListener.class)
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<?> createNotice(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody NoticeCreateRequest request
    ) {
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        Long userId = authUser.id();
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
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody NoticeCreateRequest request
    ) {
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        Long userId = authUser.id();
        noticeService.updateNotice(noticeId, userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        Long userId = authUser.id();
        noticeService.deleteNotice(noticeId, userId);
        return ResponseEntity.noContent().build();
    }
}