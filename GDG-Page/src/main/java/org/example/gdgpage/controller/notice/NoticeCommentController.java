package org.example.gdgpage.controller.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
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
    public ResponseEntity<Long> createComment(
                                            @PathVariable Long noticeId,
                                            @AuthenticationPrincipal AuthUser authUser,
                                            @RequestBody NoticeCommentCreateRequest request) {


        Long commentId = noticeCommentService.createComment(noticeId, authUser.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }

    @GetMapping
    public ResponseEntity<List<NoticeCommentResponse>> getComments(@PathVariable Long noticeId) {
        List<NoticeCommentResponse> responses = noticeCommentService.getCommentsByNotice(noticeId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long noticeId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody NoticeCommentUpdateRequest request) {

        noticeCommentService.updateComment(commentId,authUser.id(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment (
    @PathVariable Long noticeId,
    @PathVariable Long commentId,
    @AuthenticationPrincipal AuthUser authUser
            ) {
        noticeCommentService.deleteComment(commentId,authUser.id());
        return ResponseEntity.noContent().build();
    }
}