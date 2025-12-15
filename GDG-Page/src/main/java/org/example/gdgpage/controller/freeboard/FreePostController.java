package org.example.gdgpage.controller.freeboard;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
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
            @RequestBody FreePostCreateRequestDto dto,
            @RequestAttribute User user
    ) {
        return freePostService.createPost(dto, user);
    }

    @PatchMapping("/{postId}")
    public FreePostResponseDto updatePost(
            @PathVariable Long postId,
            @RequestBody FreePostUpdateRequestDto dto,
            @RequestAttribute User user
    ) {
        return freePostService.updatePost(postId, dto, user);
    }

    @GetMapping("/{postId}")
    public FreePostResponseDto getPost(@PathVariable Long postId) {
        return freePostService.getPost(postId);
    }

    @GetMapping
    public List<FreePostListResponseDto> getPostList() {
        return freePostService.getPostList();
    }

    @DeleteMapping("/{postId}")
    public void deletePost(
            @PathVariable Long postId,
            @RequestAttribute User user
    ) {
        freePostService.deletePost(postId, user);
    }

}
