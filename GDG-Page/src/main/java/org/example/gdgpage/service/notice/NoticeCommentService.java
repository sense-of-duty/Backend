package org.example.gdgpage.service.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.domain.notice.entity.NoticeComment;
import org.example.gdgpage.domain.notice.repository.NoticeRepository;
import org.example.gdgpage.domain.notice.repository.NoticeCommentRepository;
import org.example.gdgpage.dto.notice.request.comment.NoticeCommentCreateRequest;
import org.example.gdgpage.dto.notice.request.comment.NoticeCommentUpdateRequest;
import org.example.gdgpage.dto.notice.response.comment.NoticeCommentResponse;
import org.example.gdgpage.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        NoticeComment parent = null;
        if (request.parentId() != null) {
            parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("답글이 존재하지 않습니다."));
        }

        NoticeComment comment = NoticeComment.builder()
                .content(request.content())
                .isAnonymous(request.isAnonymous())
                .postType(request.postType())
                .authorId(authorId)
                .notice(notice)
                .parent(parent)
                .build();

        return commentRepository.save(comment).getId();
    }

    public List<NoticeCommentResponse> getCommentsByNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        return commentRepository.findByNoticeAndParentIsNullOrderByCreatedAtAsc(notice)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Long commentId, NoticeCommentUpdateRequest request) {
        NoticeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        comment.updateContent(request.content());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        NoticeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        commentRepository.delete(comment);
    }

    private NoticeCommentResponse convertToResponse(NoticeComment comment) {

        String authorName = userRepository.findById(comment.getAuthorId())
                .map(User::getName)
                .orElse("탈퇴한 사용자");


        if (comment.isAnonymous()) {
            authorName = "익명";
        }

        return NoticeCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .authorName(authorName)
                .isAnonymous(comment.isAnonymous())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .children(comment.getChildren().stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}