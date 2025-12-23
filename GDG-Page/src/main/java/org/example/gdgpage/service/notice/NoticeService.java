package org.example.gdgpage.service.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.domain.notice.repository.NoticeRepository;
import org.example.gdgpage.dto.notice.request.post.NoticeCreateRequest;
import org.example.gdgpage.dto.notice.response.NoticeListResponse;
import org.example.gdgpage.dto.notice.response.post.NoticeResponse;
import org.example.gdgpage.repository.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createNotice(Long authorId, NoticeCreateRequest request) {
        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .isPinned(request.isPinned())
                .partId(request.partId())
                .authorId(authorId)
                .viewCount(0)
                .build();

        return noticeRepository.save(notice).getId();
    }

    public List<NoticeListResponse> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(notice -> NoticeListResponse.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .authorId(notice.getAuthorId())
                        .partId(notice.getPartId())
                        .viewCount(notice.getViewCount())
                        .isPinned(notice.isPinned())
                        .createdAt(notice.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public NoticeResponse getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 작성자 이름 매핑 로직 추가
        String authorName = userRepository.findById(notice.getAuthorId())
                .map(User::getName)
                .orElse("탈퇴한 사용자");

        notice.incrementViewCount();

        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .partId(notice.getPartId())
                .authorId(notice.getAuthorId())
                .authorName(authorName)
                .viewCount(notice.getViewCount())
                .isPinned(notice.isPinned())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    @Transactional
    public void updateNotice(Long noticeId, Long userId, NoticeCreateRequest request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!notice.getAuthorId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 엔티티의 updateNotice 메서드에도 request.partId()를 추가해야 합니다
        notice.updateNotice(request.title(), request.content(), request.isPinned());
    }

    @Transactional
    public void deleteNotice(Long noticeId, Long userId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!notice.getAuthorId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        noticeRepository.delete(notice);
    }
}