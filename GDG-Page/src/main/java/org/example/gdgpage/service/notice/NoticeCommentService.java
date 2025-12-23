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
import org.example.gdgpage.repository.auth.UserRepository;
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

            if (!parent.getNotice().getId().equals(notice.getId())) {
                throw new IllegalArgumentException("다른 공지사항의 댓글에는 답글을 달 수 없습니다.");
            }
        }

        NoticeComment comment = NoticeComment.builder()
                .content(request.content())
                .isAnonymous(request.isAnonymous())
                .authorId(authorId)
                .notice(notice)
                .parent(parent)
                .build();

        return commentRepository.save(comment).getId();
    }

    public List<NoticeCommentResponse> getCommentsByNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // Entity의 @BatchSize(size=100) 덕분에 N+1 문제 없이 효율적으로 가져옵니다.
        return commentRepository.findByNoticeAndParentIsNullOrderByCreatedAtAsc(notice)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 2. 권한 검증 추가 (리뷰 반영)
    @Transactional
    public void updateComment(Long commentId, Long userId, NoticeCommentUpdateRequest request) { // userId 파라미터 추가
        NoticeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 본인 확인
        if (!comment.getAuthorId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        comment.updateContent(request.content());
    }

    // 3. 권한 검증 및 Soft Delete 적용 (리뷰 반영)
    @Transactional
    public void deleteComment(Long commentId, Long userId) { // userId 파라미터 추가
        NoticeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 본인 확인
        if (!comment.getAuthorId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // DB 삭제 대신, 엔티티의 delete() 메서드 호출 (Java 시간으로 처리)
        comment.delete();
    }

    private NoticeCommentResponse convertToResponse(NoticeComment comment) {
        // 작성자 이름 매핑
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