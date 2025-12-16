package org.example.gdgpage.service.freeboard;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.freeboard.FreeComment;
import org.example.gdgpage.domain.freeboard.FreePost;
import org.example.gdgpage.dto.freeboard.request.FreeCommentCreateRequestDto;
import org.example.gdgpage.dto.freeboard.request.FreeCommentUpdateRequestDto;
import org.example.gdgpage.dto.freeboard.response.FreeCommentResponseDto;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
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
    private final FindUserImpl findUserImpl;

    @Transactional
    public FreeCommentResponseDto createComment(Long postId, FreeCommentCreateRequestDto dto, String refreshToken) {

        Long userId = findUserImpl.getUserIdFromRefreshToken(refreshToken);
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
        }

        FreeComment comment = new FreeComment(post, author, dto.content(), dto.isAnonymous());
        comment.setParent(parent);

        return new FreeCommentResponseDto(freeCommentRepository.save(comment));
    }

    @Transactional
    public FreeCommentResponseDto updateComment(Long commentId, FreeCommentUpdateRequestDto dto, String refreshToken) {

        Long userId = findUserImpl.getUserIdFromRefreshToken(refreshToken);
        User author = findUserImpl.getUserById(userId);

        FreeComment comment = freeCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));

        // 권한 확인 (작성자 또는 관리자)
        if (!comment.getAuthor().getId().equals(author.getId()) &&
                author.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException(ErrorMessage.EMPTY_COMMENT);
        }

        comment.update(dto.content());

        return new FreeCommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String refreshToken) {

        Long userId = findUserImpl.getUserIdFromRefreshToken(refreshToken);
        User author = findUserImpl.getUserById(userId);

        FreeComment comment = freeCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));

        if (!comment.getAuthor().getId().equals(author.getId()) &&
                author.getRole() != Role.ORGANIZER) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        comment.delete();
        freeCommentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<FreeCommentResponseDto> getCommentTree(Long postId, String refreshToken) {

        FreePost post = freePostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        List<FreeComment> comments = freeCommentRepository
                .findByPostOrderByCreatedAtAsc(post);

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
}
