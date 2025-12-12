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
import org.example.gdgpage.exception.ErrorMessage;
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
            throw new BadRequestException(ErrorMessage.EMPTY_TITLE);
        }
        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException(ErrorMessage.EMPTY_CONTENT);
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
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        if (!post.getAuthor().getId().equals(author.getId()) &&
                author.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        if (dto.title() == null || dto.title().isBlank()) {
            throw new BadRequestException(ErrorMessage.EMPTY_TITLE);
        }
        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException(ErrorMessage.EMPTY_CONTENT);
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
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

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
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        if (!post.getAuthor().getId().equals(author.getId()) &&
                author.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        freePostRepository.delete(post);
    }
}
