package org.example.gdgpage.controller.freeboard;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.freeboard.request.AdminPostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreePostListResponseDto;
import org.example.gdgpage.dto.freeboard.response.FreePostResponseDto;
import org.example.gdgpage.service.freeboard.FreePostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts")
public class FreePostController {

    private final FreePostService freePostService;

    @PostMapping
    public ResponseEntity<FreePostResponseDto> createPost(@AuthenticationPrincipal AuthUser authUser,
                                                          @Valid @RequestBody FreePostCreateRequestDto dto) {
        FreePostResponseDto response = freePostService.createUserPost(dto, authUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<FreePostResponseDto> createAdminPost(@AuthenticationPrincipal AuthUser authUser,
                                                               @Valid @RequestBody AdminPostCreateRequestDto dto) {
        FreePostResponseDto response = freePostService.createAdminPost(dto, authUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<FreePostResponseDto> updatePost(@AuthenticationPrincipal AuthUser authUser,
                                                          @PathVariable Long postId,
                                                          @Valid @RequestBody FreePostUpdateRequestDto dto) {
        FreePostResponseDto response = freePostService.updatePost(postId, dto, authUser.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<FreePostResponseDto> getPost(@AuthenticationPrincipal AuthUser authUser,
                                                       @PathVariable Long postId) {
        FreePostResponseDto response = freePostService.getPost(postId, authUser.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FreePostListResponseDto>> getPostList(@AuthenticationPrincipal AuthUser authUser) {
        List<FreePostListResponseDto> response = freePostService.getPostList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId) {
        freePostService.deletePost(postId, authUser.id());
        return ResponseEntity.noContent().build();
    }
}
