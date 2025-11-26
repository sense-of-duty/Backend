package org.example.gdgpage.service;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.freeboard.FreePost;
import org.example.gdgpage.dto.freeboard.request.FreePostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreePostListResponseDto;
import org.example.gdgpage.dto.freeboard.response.FreePostResponseDto;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.repository.freeboard.FreePostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.gdgpage.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreePostService {

    private final FreePostRepository freePostRepository;
    private final UserRepository userRepository;

    @Transactional
    public FreePostResponseDto createPost(FreePostCreateRequestDto dto, User author) {

        if (dto.title() == null || dto.title().isBlank()) {
            throw new BadRequestException("제목이 비어있습니다.");
        }
        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException("본문이 비어있습니다.");
        }

        FreePost post;

        if (author.getRole() == Role.ORGANIZER) {
            post = FreePost.createByAdmin(
                    author,
                    dto.title(),
                    dto.content(),
                    dto.isAnonymous(),
                    dto.isPinned()
            );
        } else {
            post = FreePost.create(
                    author,
                    dto.title(),
                    dto.content(),
                    dto.isAnonymous()
            );
        }

        return new FreePostResponseDto(freePostRepository.save(post));
    }

    @Transactional
    public FreePostResponseDto updatePost(Long postId, FreePostUpdateRequestDto dto, User author) {

        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글이 존재하지 않습니다."));

        if (!post.getAuthor().getId().equals(author.getId()) &&
                author.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }

        if (dto.title() == null || dto.title().isBlank()) {
            throw new BadRequestException("제목이 비어있습니다.");
        }
        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException("본문이 비어있습니다.");
        }

        if (author.getRole() == Role.ORGANIZER) {
            post.updateByAdmin(dto.title(), dto.content(), dto.isPinned());
        } else {
            post.update(dto.title(), dto.content());
        }

        return new FreePostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public FreePostResponseDto getPost(Long postId) {
        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글이 존재하지 않습니다."));

        post.increaseViewCount();

        return new FreePostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public List<FreePostListResponseDto> getPostList() {

        return freePostRepository.findAll().stream()
                .sorted(Comparator.comparing(FreePost::getCreatedAt).reversed())
                .map(FreePostListResponseDto::new)
                .toList();
    }

    @Transactional
    public void deletePost(Long postId, User author) {

        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글이 존재하지 않습니다."));

        if (!post.getAuthor().getId().equals(author.getId()) &&
                author.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException("삭제 권한이 없습니다.");
        }

        freePostRepository.delete(post);
    }
}
