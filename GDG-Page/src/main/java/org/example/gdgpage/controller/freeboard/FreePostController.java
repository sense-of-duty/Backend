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
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody FreePostCreateRequestDto dto
    ) {
        FreePostResponseDto response = freePostService.createUserPost(dto, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<FreePostResponseDto> createAdminPost(
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody AdminPostCreateRequestDto dto
    ) {
        FreePostResponseDto response = freePostService.createAdminPost(dto, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<FreePostResponseDto> updatePost(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long postId,
            @Valid @RequestBody FreePostUpdateRequestDto dto
    ) {
        FreePostResponseDto response = freePostService.updatePost(postId, dto, accessToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<FreePostResponseDto> getPost(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long postId
    ) {
        FreePostResponseDto response = freePostService.getPost(postId, accessToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FreePostListResponseDto>> getPostList(
            @RequestHeader("Authorization") String accessToken
    ) {
        List<FreePostListResponseDto> response = freePostService.getPostList(accessToken);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long postId
    ) {
        freePostService.deletePost(postId, accessToken);
        return ResponseEntity.noContent().build();
    }
}
