package org.example.gdgpage.service.freeboard;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.freeboard.FreePost;
import org.example.gdgpage.domain.freeboard.FreePostLike;
import org.example.gdgpage.dto.freeboard.request.AdminPostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.AdminPostUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreePostUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreePostListResponseDto;
import org.example.gdgpage.dto.freeboard.response.FreePostResponseDto;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.repository.auth.UserRepository;
import org.example.gdgpage.repository.freeboard.FreePostLikeRepository;
import org.example.gdgpage.repository.freeboard.FreePostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreePostService {

    private final FreePostRepository freePostRepository;
    private final FreePostLikeRepository freePostLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public FreePostResponseDto createUserPost(FreePostCreateRequestDto dto, Long userId) {
        User author = getUser(userId);

        if (author.getRole() == Role.ORGANIZER) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION_USER_POST);
        }

        FreePost post = FreePost.create(
                author,
                dto.title(),
                dto.content(),
                dto.isAnonymous()
        );

        return new FreePostResponseDto(freePostRepository.save(post));
    }

    @Transactional
    public FreePostResponseDto createAdminPost(AdminPostCreateRequestDto dto, Long userId) {
        User author = getUser(userId);

        if (author.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION_ADMIN_POST);
        }

        FreePost post = FreePost.createByAdmin(
                author,
                dto.title(),
                dto.content(),
                dto.isAnonymous(),
                dto.isPinned()
        );

        return new FreePostResponseDto(freePostRepository.save(post));
    }

    @Transactional
    public FreePostResponseDto updatePost(Long postId, FreePostUpdateRequestDto dto, Long userId) {

        FreePost post = getPostWithPermissionCheck(postId, userId);
        User user = getUser(userId);

        if (dto.title() == null || dto.title().isBlank()) {
            throw new BadRequestException(ErrorMessage.EMPTY_TITLE);
        }
        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException(ErrorMessage.EMPTY_CONTENT);
        }

        post.update(dto.title(), dto.content());

        return new FreePostResponseDto(post);
    }

    @Transactional
    public FreePostResponseDto updatePostByAdmin(Long postId, AdminPostUpdateRequestDto dto, Long userId
    ) {
        User admin = getUser(userId);

        if (admin.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        post.updateByAdmin(dto.title(), dto.content(), dto.isPinned());

        return new FreePostResponseDto(post);
    }

    @Transactional
    public FreePostResponseDto getPost(Long postId, Long userId) {

        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        post.increaseViewCount();

        return new FreePostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public List<FreePostListResponseDto> getPostList() {

        List<FreePost> posts = freePostRepository.findAllWithAuthor();

        return posts.stream()
                .map(FreePostListResponseDto::new)
                .toList();
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {

        FreePost post = getPostWithPermissionCheck(postId, userId);
        freePostRepository.delete(post);
    }

    private FreePost getPostWithPermissionCheck(Long postId, Long userId) {
        User author = getUser(userId);

        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        if (!post.getAuthor().getId().equals(author.getId()) &&
                author.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        return post;
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        User user = getUser(userId);
        FreePost post = getLikeablePost(postId);

        if (freePostLikeRepository.existsByUserAndPost(user, post)) {
            throw new BadRequestException(ErrorMessage.ALREADY_LIKED);
        }

        freePostLikeRepository.save(new FreePostLike(user, post));
        post.increaseLikeCount();
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        User user = getUser(userId);
        FreePost post = getLikeablePost(postId);

        FreePostLike like = freePostLikeRepository
                .findByUserAndPost(user, post)
                .orElseThrow(() ->
                        new BadRequestException(ErrorMessage.NOT_LIKED));

        freePostLikeRepository.delete(like);
        post.decreaseLikeCount();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));
    }

    private FreePost getLikeablePost(Long postId) {
        return freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));
    }
}
