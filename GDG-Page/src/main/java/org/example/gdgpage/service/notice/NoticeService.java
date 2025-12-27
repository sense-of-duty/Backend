package org.example.gdgpage.service.notice;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.notice.entity.Notice;
import org.example.gdgpage.dto.notice.request.post.NoticeCreateRequest;
import org.example.gdgpage.dto.notice.request.post.NoticeUpdateRequest;
import org.example.gdgpage.dto.notice.response.NoticeListResponse;
import org.example.gdgpage.dto.notice.response.post.NoticeResponse;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.repository.auth.UserRepository;
import org.example.gdgpage.repository.notice.NoticeRepository;
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
    public NoticeResponse createNotice(Long authorId, NoticeCreateRequest request) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        if (!user.getRole().isAdmin()) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        if (user.getRole() == Role.CORE) {
            if (user.getPart() != request.partId()) {
                throw new ForbiddenException(ErrorMessage.ACCESS_DENY);
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

        Notice savedNotice = noticeRepository.save(notice);

        return NoticeResponse.builder()
                .id(savedNotice.getId())
                .title(savedNotice.getTitle())
                .content(savedNotice.getContent())
                .partId(savedNotice.getPartId())
                .authorId(user.getId())
                .authorName(user.getName())
                .viewCount(savedNotice.getViewCount())
                .isPinned(savedNotice.isPinned())
                .createdAt(savedNotice.getCreatedAt())
                .updatedAt(savedNotice.getUpdatedAt())
                .build();
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
        noticeRepository.updateViewCount(noticeId);

        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .partId(notice.getPartId())
                .authorId(notice.getAuthor().getId())
                .authorName(notice.getAuthor().getName())
                .viewCount(notice.getViewCount())
                .isPinned(notice.isPinned())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    @Transactional
    public void updateNotice(Long noticeId, Long userId, NoticeUpdateRequest request) {
        Notice notice = findNoticeWithPermission(noticeId, userId);

        notice.updateNotice(
                request.title(),
                request.content(),
                request.isPinned(),
                request.partId()
        );
    }

    @Transactional
    public void deleteNotice(Long noticeId, Long userId) {
        Notice notice = findNoticeWithPermission(noticeId, userId);

        notice.delete();
    }


    private Notice findNoticeWithPermission(Long noticeId, Long userId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        if (!notice.getAuthor().getId().equals(userId) && !user.getRole().isAdmin()) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        return notice;
    }
}