package org.example.gdgpage.controller.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser; // PrincipalDetails 대신 AuthUser 임포트
import org.example.gdgpage.dto.notice.request.comment.NoticeCommentCreateRequest;
import org.example.gdgpage.dto.notice.request.comment.NoticeCommentUpdateRequest;
import org.example.gdgpage.dto.notice.response.comment.NoticeCommentResponse;
import org.example.gdgpage.service.notice.NoticeCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices/{noticeId}/comments")
public class NoticeCommentController {

    private final NoticeCommentService noticeCommentService;

    @PostMapping
    public ResponseEntity<?> createComment( // 리턴 타입을 ResponseEntity<?>로 유연하게 변경
                                            @PathVariable Long noticeId,
                                            @AuthenticationPrincipal AuthUser authUser, // AuthUser로 변경
                                            @RequestBody NoticeCommentCreateRequest request) {

        // 로그인 체크 추가
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        // 팀원들의 AuthUser 필드명(userId 또는 id)에 맞춰서 호출하세요.
        Long authorId = authUser.id();
        Long commentId = noticeCommentService.createComment(noticeId, authorId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }

    @GetMapping
    public ResponseEntity<List<NoticeCommentResponse>> getComments(@PathVariable Long noticeId) {
        List<NoticeCommentResponse> responses = noticeCommentService.getCommentsByNotice(noticeId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody NoticeCommentUpdateRequest request) {

        noticeCommentService.updateComment(commentId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        noticeCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}