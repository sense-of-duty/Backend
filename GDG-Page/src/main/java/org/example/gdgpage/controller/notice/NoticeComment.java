package org.example.gdgpage.controller.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.PrincipalDetails;
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
public class NoticeComment {

    private final NoticeCommentService noticeCommentService;

    @PostMapping
    public ResponseEntity<Long> createComment(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody NoticeCommentCreateRequest request) {

        Long authorId = principalDetails.getUser().getId();
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