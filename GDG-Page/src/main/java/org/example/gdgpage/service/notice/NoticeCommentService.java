package org.example.gdgpage.service.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.domain.notice.entity.NoticeComment;
import org.example.gdgpage.repository.notice.NoticeRepository;
import org.example.gdgpage.repository.notice.NoticeCommentRepository;
import org.example.gdgpage.dto.notice.request.comment.NoticeCommentCreateRequest;
import org.example.gdgpage.dto.notice.request.comment.NoticeCommentUpdateRequest;
import org.example.gdgpage.dto.notice.response.comment.NoticeCommentResponse;
import org.example.gdgpage.repository.auth.UserRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        NoticeComment parent = null;
        if (request.parentId() != null) {
            parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("답글이 존재하지 않습니다."));

            if (!parent.getNotice().getId().equals(notice.getId())) {
                throw new IllegalArgumentException("다른 공지사항의 댓글에는 답글을 달 수 없습니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getNotice().getId().equals(noticeId)) {
            throw new IllegalArgumentException("해당 공지사항의 댓글이 아닙니다.");
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        comment.updateContent(request.content());
    }

    @Transactional
    public void deleteComment(Long noticeId, Long commentId, Long userId) {
        NoticeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getNotice().getId().equals(noticeId)) {
            throw new IllegalArgumentException("해당 공지사항의 댓글이 아닙니다.");
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        comment.delete();
    }

}