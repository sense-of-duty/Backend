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
public class Notice {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<Long> createNotice(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody NoticeCreateRequest request
    ) {
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
    public ResponseEntity<Void> updateNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody NoticeCreateRequest request
    ) {
        Long userId = principalDetails.getUser().getId();
        noticeService.updateNotice(noticeId, userId, request);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUser().getId();
        noticeService.deleteNotice(noticeId, userId);
        return ResponseEntity.noContent().build();
    }
}