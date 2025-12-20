package org.example.gdgpage.service.freeboard;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.freeboard.FreeComment;
import org.example.gdgpage.domain.freeboard.FreeCommentLike;
import org.example.gdgpage.domain.freeboard.FreePost;
import org.example.gdgpage.dto.freeboard.request.FreeCommentCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreeCommentUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreeCommentResponseDto;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.repository.freeboard.FreeCommentLikeRepository;
import org.example.gdgpage.repository.freeboard.FreeCommentRepository;
import org.example.gdgpage.repository.freeboard.FreePostRepository;
import org.example.gdgpage.service.finder.FindUserImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreeCommentService {

    private final FreeCommentRepository freeCommentRepository;
    private final FreePostRepository freePostRepository;
    private final FreeCommentLikeRepository freeCommentLikeRepository;
    private final FindUserImpl findUserImpl;

    @Transactional
    public void likeComment(Long commentId, Long userId) {
        User user = findUserImpl.getUserById(userId);
        FreeComment comment = getLikeableComment(commentId);

        if (freeCommentLikeRepository.existsByUserAndComment(user, comment)) {
            throw new BadRequestException(ErrorMessage.ALREADY_LIKED);
        }

        freeCommentLikeRepository.save(new FreeCommentLike(user, comment));
        comment.increaseLikeCount();
    }

    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        User user = findUserImpl.getUserById(userId);
        FreeComment comment = getLikeableComment(commentId);

        FreeCommentLike like = freeCommentLikeRepository
                .findByUserAndComment(user, comment)
                .orElseThrow(() ->
                        new BadRequestException(ErrorMessage.NOT_LIKED));

        freeCommentLikeRepository.delete(like);
        comment.decreaseLikeCount();
    }

    @Transactional
    public FreeCommentResponseDto createComment(Long postId, FreeCommentCreateRequestDto dto, Long userId) {

        User author = findUserImpl.getUserById(userId);

        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException(ErrorMessage.EMPTY_COMMENT);
        }

        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        FreeComment parent = null;
        if (dto.parentId() != null) {
            parent = freeCommentRepository.findById(dto.parentId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_PARENT_COMMENT));

            if (!parent.getPost().getId().equals(postId)) {
                throw new BadRequestException(ErrorMessage.INVALID_PARENT_COMMENT);
            }
        }

        FreeComment comment = new FreeComment(post, author, dto.content(), dto.isAnonymous());
        comment.setParent(parent);

        return new FreeCommentResponseDto(freeCommentRepository.save(comment));
    }

    @Transactional
    public FreeCommentResponseDto updateComment(Long postId, Long commentId, FreeCommentUpdateRequestDto dto, Long userId) {

        FreeComment comment = getCommentWithPermissionCheck(commentId, userId);

        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException(ErrorMessage.EMPTY_COMMENT);
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new BadRequestException(ErrorMessage.COMMENT_POST_MISMATCH);
        }

        comment.update(dto.content());

        return new FreeCommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {

        FreeComment comment = getCommentWithPermissionCheck(commentId, userId);

        if (!comment.getPost().getId().equals(postId)) {
            throw new BadRequestException(ErrorMessage.COMMENT_POST_MISMATCH);
        }

        comment.delete();
    }

    @Transactional(readOnly = true)
    public List<FreeCommentResponseDto> getCommentTree(Long postId, Long userId) {

        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        List<FreeComment> comments = freeCommentRepository
                .findByPostWithAuthorAndParent(post);

        List<FreeCommentResponseDto> dtoList = comments.stream()
                .map(FreeCommentResponseDto::new)
                .toList();

        Map<Long, FreeCommentResponseDto> map = dtoList.stream()
                .collect(Collectors.toMap(FreeCommentResponseDto::getId, dto -> dto));

        List<FreeCommentResponseDto> roots = new ArrayList<>();

        for (FreeCommentResponseDto dto : dtoList) {
            if (dto.getParentId() == null) {
                roots.add(dto);
            } else {
                FreeCommentResponseDto parent = map.get(dto.getParentId());
                if (parent != null) {
                    parent.addChild(dto);
                }
            }
        }

        return roots;
    }

    private FreeComment getCommentWithPermissionCheck(Long commentId, Long userId) {
        User user = findUserImpl.getUserById(userId);

        FreeComment comment = freeCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));

        if (comment.isDeleted()) {
            throw new BadRequestException(ErrorMessage.NOT_EXIST_COMMENT);
        }

        boolean isOwner = comment.getAuthor().getId().equals(user.getId());
        boolean isOrganizer = user.getRole() == Role.ORGANIZER;

        if (!isOwner && !isOrganizer) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        return comment;
    }

    private FreeComment getLikeableComment(Long commentId) {
        FreeComment comment = freeCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));

        if (comment.isDeleted()) {
            throw new BadRequestException(ErrorMessage.NOT_EXIST_COMMENT);
        }

        return comment;
    }

}
