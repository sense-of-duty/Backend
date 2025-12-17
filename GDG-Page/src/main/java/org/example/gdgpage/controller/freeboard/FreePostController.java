package org.example.gdgpage.controller.freeboard;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.freeboard.request.AdminPostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreePostListResponseDto;
import org.example.gdgpage.dto.freeboard.response.FreePostResponseDto;
import org.example.gdgpage.service.freeboard.FreePostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts")
public class FreePostController {

    private final FreePostService freePostService;

    @PostMapping
    public ResponseEntity<FreePostResponseDto> createPost(
            @RequestHeader("refreshToken") String refreshToken,
            @Valid @RequestBody FreePostCreateRequestDto dto
    ) {
        FreePostResponseDto response = freePostService.createUserPost(dto, refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<FreePostResponseDto> createAdminPost(
            @RequestHeader("refreshToken") String refreshToken,
            @Valid @RequestBody AdminPostCreateRequestDto dto
    ) {
        FreePostResponseDto response = freePostService.createAdminPost(dto, refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<FreePostResponseDto> updatePost(
            @RequestHeader("refreshToken") String refreshToken,
            @PathVariable Long postId,
            @Valid @RequestBody FreePostUpdateRequestDto dto
    ) {
        FreePostResponseDto response = freePostService.updatePost(postId, dto, refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<FreePostResponseDto> getPost(
            @RequestHeader("refreshToken") String refreshToken,
            @PathVariable Long postId
    ) {
        FreePostResponseDto response = freePostService.getPost(postId, refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FreePostListResponseDto>> getPostList(
            @RequestHeader("refreshToken") String refreshToken
    ) {
        List<FreePostListResponseDto> response = freePostService.getPostList(refreshToken);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @RequestHeader("refreshToken") String refreshToken,
            @PathVariable Long postId
    ) {
        freePostService.deletePost(postId, refreshToken);
        return ResponseEntity.noContent().build();
    }
}
