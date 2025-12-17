package org.example.gdgpage.controller.freeboard;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.dto.freeboard.request.FreeCommentCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreeCommentUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreeCommentResponseDto;
import org.example.gdgpage.service.freeboard.FreeCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts/{postId}/comments")
public class FreeCommentController {

    private final FreeCommentService freeCommentService;

    @PostMapping
    public ResponseEntity<FreeCommentResponseDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody FreeCommentCreateRequestDto dto,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        FreeCommentResponseDto response = freeCommentService.createComment(postId, dto, refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<FreeCommentResponseDto> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody FreeCommentUpdateRequestDto dto,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        FreeCommentResponseDto response = freeCommentService.updateComment(commentId, dto, refreshToken);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        freeCommentService.deleteComment(commentId, refreshToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FreeCommentResponseDto>> getComments(
            @PathVariable Long postId,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        List<FreeCommentResponseDto> response = freeCommentService.getCommentTree(postId, refreshToken);
        return ResponseEntity.ok(response);
    }
}
