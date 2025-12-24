package org.example.gdgpage.service.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.dto.notice.request.post.NoticeUpdateRequest;
import org.example.gdgpage.repository.notice.NoticeRepository;
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
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!user.getRole().isAdmin()) {
            throw new RuntimeException("공지사항 작성 권한이 없습니다.");
        }

        if (user.getRole() == Role.CORE) {
            if (user.getPart() != request.partId()) {
                throw new IllegalArgumentException("CORE 멤버는 자신의 소속 파트(" + user.getPart() + ") 공지만 작성할 수 있습니다.");
            }
        }

        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .isPinned(request.isPinned())
                .partId(request.partId())
                .author(user)
                .viewCount(0)
                .build();

        return noticeRepository.save(notice).getId();
    }

    public List<NoticeListResponse> getAllNotices() {
        return noticeRepository.findAllByDeletedAtIsNullOrderByIsPinnedDescCreatedAtDesc()
                .stream()
                .map(notice -> NoticeListResponse.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .authorId(notice.getAuthor().getId())
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

        User author = notice.getAuthor();

        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .partId(notice.getPartId())
                .authorId(author.getId())
                .authorName(author.getName())
                .viewCount(notice.getViewCount())
                .isPinned(notice.isPinned())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    @Transactional
    public void updateNotice(Long noticeId, Long userId, NoticeUpdateRequest request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!notice.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        notice.updateNotice(
                request.title(),
                request.content(),
                request.isPinned(),
                request.partId()
        );
    }

    @Transactional
    public void deleteNotice(Long noticeId, Long userId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!notice.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        notice.delete();
    }
}