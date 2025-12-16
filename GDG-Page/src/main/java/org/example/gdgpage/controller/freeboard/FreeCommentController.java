package org.example.gdgpage.controller.freeboard;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.freeboard.request.FreeCommentCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreeCommentUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreeCommentResponseDto;
import org.example.gdgpage.service.freeboard.FreeCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts/{postId}/comments")
public class FreeCommentController {

    private final FreeCommentService freeCommentService;

    @PostMapping
    public FreeCommentResponseDto createComment(
            @PathVariable Long postId,
            @RequestBody FreeCommentCreateRequestDto dto,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        return freeCommentService.createComment(postId, dto, refreshToken);
    }

    @PatchMapping("/{commentId}")
    public FreeCommentResponseDto updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody FreeCommentUpdateRequestDto dto,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        return freeCommentService.updateComment(commentId, dto, refreshToken);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        freeCommentService.deleteComment(commentId, refreshToken);
    }

    @GetMapping
    public List<FreeCommentResponseDto> getComments(
            @PathVariable Long postId,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        return freeCommentService.getCommentTree(postId, refreshToken);
    }
}
