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
            @RequestAttribute String userId
    ) {
        return freeCommentService.createComment(postId, dto, userId);
    }

    @PatchMapping("/{commentId}")
    public FreeCommentResponseDto updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody FreeCommentUpdateRequestDto dto,
            @RequestAttribute String userId
    ) {
        return freeCommentService.updateComment(commentId, dto, userId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestAttribute String userId
    ) {
        freeCommentService.deleteComment(commentId, userId);
    }

    @GetMapping
    public List<FreeCommentResponseDto> getComments(
            @PathVariable Long postId
    ) {
        return freeCommentService.getCommentTree(postId);
    }
}
