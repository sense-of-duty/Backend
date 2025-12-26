package org.example.gdgpage.controller.freeboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.freeboard.request.AdminPostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.AdminPostUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreePostListResponseDto;
import org.example.gdgpage.dto.freeboard.response.FreePostResponseDto;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.service.freeboard.FreePostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts")
public class FreePostController {

    private final FreePostService freePostService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<FreePostResponseDto> createPost(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart("data") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        FreePostCreateRequestDto dto = parseAndValidate(requestJson, FreePostCreateRequestDto.class);
        FreePostResponseDto response = freePostService.createUserPost(dto, authUser.id(), image);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/admin", consumes = "multipart/form-data")
    public ResponseEntity<FreePostResponseDto> createAdminPost(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart("data") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        AdminPostCreateRequestDto dto = parseAndValidate(requestJson, AdminPostCreateRequestDto.class);
        FreePostResponseDto response = freePostService.createAdminPost(dto, authUser.id(), image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<FreePostResponseDto> updatePost(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long postId,
            @RequestPart("data") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        FreePostUpdateRequestDto dto = parseAndValidate(requestJson, FreePostUpdateRequestDto.class);
        FreePostResponseDto response = freePostService.updatePost(postId, dto, authUser.id(), image);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/admin/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<FreePostResponseDto> updatePostByAdmin(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long postId,
            @RequestPart("data") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        AdminPostUpdateRequestDto dto = parseAndValidate(requestJson, AdminPostUpdateRequestDto.class);
        FreePostResponseDto response = freePostService.updatePostByAdmin(postId, dto, authUser.id(), image);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<FreePostResponseDto> getPost(@PathVariable Long postId) {
        FreePostResponseDto response = freePostService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FreePostListResponseDto>> getPostList(@RequestParam(required = false, defaultValue = "") String keyword) {
        List<FreePostListResponseDto> response = freePostService.getPostList(keyword);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId) {
        freePostService.deletePost(postId, authUser.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> likePost(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long postId
    ) {
        freePostService.likePost(postId, authUser.id());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlikePost(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long postId
    ) {
        freePostService.unlikePost(postId, authUser.id());
        return ResponseEntity.noContent().build();
    }

    private <T> T parseAndValidate(String json, Class<T> clazz) {
        try {
            T dto = objectMapper.readValue(json, clazz);

            BindingResult errors = new BeanPropertyBindingResult(dto, clazz.getSimpleName());
            validator.validate(dto, errors);

            if (errors.hasErrors()) {
                throw new BadRequestException(ErrorMessage.INVALID_FREEPOST_INPUT);
            }

            return dto;

        } catch (JsonProcessingException e) {
            throw new BadRequestException(ErrorMessage.INVALID_FREEPOST_INPUT);
        }
    }
}
