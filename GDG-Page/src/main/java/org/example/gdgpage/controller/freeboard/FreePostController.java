package org.example.gdgpage.controller.freeboard;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.freeboard.request.AdminPostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreePostListResponseDto;
import org.example.gdgpage.dto.freeboard.response.FreePostResponseDto;
import org.example.gdgpage.service.freeboard.FreePostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts")
public class FreePostController {

    private final FreePostService freePostService;

    @PostMapping
    public FreePostResponseDto createPost(
            @RequestHeader("refreshToken") String refreshToken,
            @RequestBody FreePostCreateRequestDto dto
    ) {
        return freePostService.createUserPost(dto, refreshToken);
    }

    @PostMapping("/admin")
    public FreePostResponseDto createAdminPost(
            @RequestHeader("refreshToken") String refreshToken,
            @RequestBody AdminPostCreateRequestDto dto
    ) {
        return freePostService.createAdminPost(dto, refreshToken);
    }

    @PatchMapping("/{postId}")
    public FreePostResponseDto updatePost(
            @RequestHeader("refreshToken") String refreshToken,
            @PathVariable Long postId,
            @RequestBody FreePostUpdateRequestDto dto
    ) {
        return freePostService.updatePost(postId, dto, refreshToken);
    }

    @GetMapping("/{postId}")
    public FreePostResponseDto getPost(
            @RequestHeader("refreshToken") String refreshToken,
            @PathVariable Long postId) {
        return freePostService.getPost(postId, refreshToken);
    }

    @GetMapping
    public List<FreePostListResponseDto> getPostList(
            @RequestHeader("refreshToken") String refreshToken
    ) {
        return freePostService.getPostList(refreshToken);
    }
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId,
                           @RequestHeader("refreshToken") String refreshToken
    ) {
        freePostService.deletePost(postId, refreshToken);
    }
}
