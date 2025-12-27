package org.example.gdgpage.service.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.domain.notice.entity.NoticeComment;
import org.example.gdgpage.dto.notice.request.comment.NoticeCommentCreateRequest;
import org.example.gdgpage.dto.notice.request.comment.NoticeCommentUpdateRequest;
import org.example.gdgpage.dto.notice.response.comment.NoticeCommentResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.repository.auth.UserRepository;
import org.example.gdgpage.repository.notice.NoticeCommentRepository;
import org.example.gdgpage.repository.notice.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeCommentService {

    private final NoticeCommentRepository commentRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createComment(Long noticeId, Long authorId, NoticeCommentCreateRequest request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        NoticeComment parent = null;
        if (request.parentId() != null) {
            parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_PARENT_COMMENT));

            if (!parent.getNotice().getId().equals(notice.getId())) {
                throw new BadRequestException(ErrorMessage.COMMENT_POST_MISMATCH);
            }
        }

        NoticeComment comment = NoticeComment.builder()
                .content(request.content())
                .author(user)
                .notice(notice)
                .parent(parent)
                .build();

        return commentRepository.save(comment).getId();
    }

    public List<NoticeCommentResponse> getCommentsByNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        List<NoticeComment> comments = commentRepository.findAllByNoticeOrderByCreatedAtAsc(notice);

        Map<Long, NoticeCommentResponse> map = new HashMap<>();
        List<NoticeCommentResponse> roots = new ArrayList<>();

        for (NoticeComment comment : comments) {
            NoticeCommentResponse dto = NoticeCommentResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .authorId(comment.getAuthor().getId())
                    .authorName(comment.getAuthor().getName())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .children(new ArrayList<>())
                    .build();

            map.put(dto.id(), dto);

            if (comment.getParent() != null) {
                NoticeCommentResponse parentDto = map.get(comment.getParent().getId());
                if (parentDto != null) {
                    parentDto.children().add(dto);
                }
            } else {
                roots.add(dto);
            }
        }

        return roots;
    }

    @Transactional
    public void updateComment(Long noticeId, Long commentId, Long userId, NoticeCommentUpdateRequest request) {
        NoticeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));

        if (!comment.getNotice().getId().equals(noticeId)) {
            throw new BadRequestException(ErrorMessage.COMMENT_POST_MISMATCH);
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        comment.updateContent(request.content());
    }

    @Transactional
    public void deleteComment(Long noticeId, Long commentId, Long userId) {
        NoticeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));

        if (!comment.getNotice().getId().equals(noticeId)) {
            throw new BadRequestException(ErrorMessage.COMMENT_POST_MISMATCH);
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        comment.delete();
    }
}
