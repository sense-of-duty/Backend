package org.example.gdgpage.controller.freeboard;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.freeboard.request.FreeCommentCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreeCommentUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreeCommentResponseDto;
import org.example.gdgpage.service.freeboard.FreeCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts/{postId}/comments")
public class FreeCommentController {

    private final FreeCommentService freeCommentService;

    @PostMapping
    public ResponseEntity<FreeCommentResponseDto> createComment(@PathVariable Long postId,
                                                                @Valid @RequestBody FreeCommentCreateRequestDto dto,
                                                                @AuthenticationPrincipal AuthUser authUser) {
        FreeCommentResponseDto response = freeCommentService.createComment(postId, dto, authUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<FreeCommentResponseDto> updateComment(@PathVariable Long postId,
                                                                @PathVariable Long commentId,
                                                                @Valid @RequestBody FreeCommentUpdateRequestDto dto,
                                                                @AuthenticationPrincipal AuthUser authUser) {
        FreeCommentResponseDto response = freeCommentService.updateComment(postId, commentId, dto, authUser.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @AuthenticationPrincipal AuthUser authUser) {
        freeCommentService.deleteComment(postId, commentId, authUser.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FreeCommentResponseDto>> getComments(@PathVariable Long postId,
                                                                    @AuthenticationPrincipal AuthUser authUser) {
        List<FreeCommentResponseDto> response = freeCommentService.getCommentTree(postId, authUser.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<Void> likeComment(@PathVariable Long postId,
                                            @PathVariable Long commentId,
                                            @AuthenticationPrincipal AuthUser authUser) {
        freeCommentService.likeComment(commentId, authUser.id());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @AuthenticationPrincipal AuthUser authUser) {
        freeCommentService.unlikeComment(commentId, authUser.id());
        return ResponseEntity.noContent().build();
    }
}
