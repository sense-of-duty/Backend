package org.example.gdgpage.controller.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.notice.request.post.NoticeCreateRequest;
import org.example.gdgpage.dto.notice.request.post.NoticeUpdateRequest;
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
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(
                                                        @AuthenticationPrincipal AuthUser authUser,
                                                        @RequestBody @Valid NoticeCreateRequest request
    ) {

        NoticeResponse response = noticeService.createNotice(authUser.id(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    @PatchMapping("/{noticeId}")
    public ResponseEntity<Void> updateNotice(
                                              @PathVariable Long noticeId,
                                              @AuthenticationPrincipal AuthUser authUser,
                                              @RequestBody @Valid NoticeUpdateRequest request
    ) {
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = authUser.id();
        noticeService.updateNotice(noticeId, userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
                                              @PathVariable Long noticeId,
                                              @AuthenticationPrincipal AuthUser authUser
    ) {
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = authUser.id();
        noticeService.deleteNotice(noticeId, userId);
        return ResponseEntity.noContent().build();
    }
}