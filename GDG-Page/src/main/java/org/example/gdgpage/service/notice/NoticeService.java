package org.example.gdgpage.service.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.domain.notice.repository.NoticeRepository;
import org.example.gdgpage.dto.notice.request.post.NoticeCreateRequest;
import org.example.gdgpage.dto.notice.response.NoticeListResponse;
import org.example.gdgpage.dto.notice.response.post.NoticeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;


    @Transactional
    public Long createNotice(Long authorId, NoticeCreateRequest request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isPinned(request.isPinned())
                .partId(request.getPartId())
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

        notice.incrementViewCount();

        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .authorId(notice.getAuthorId())
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

        notice.updateNotice(request.getTitle(), request.getContent(), request.isPinned());
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