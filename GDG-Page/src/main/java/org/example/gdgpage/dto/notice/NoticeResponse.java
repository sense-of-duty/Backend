package org.example.gdgpage.dto.notice;

import lombok.Builder;
import lombok.Getter;
import org.example.gdgpage.domain.notice.entity.Notice;

import java.time.LocalDateTime;

@Getter
public class NoticeResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final boolean isPinned;
    private final int viewCount;
    private final LocalDateTime createdAt;
    private final Long authorId;
    private final Long partId;


    @Builder
    public NoticeResponse(Long id, String title, String content, boolean isPinned, int viewCount, LocalDateTime createdAt, Long authorId, Long partId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.authorId = authorId;
        this.partId = partId;
    }


    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .isPinned(notice.isPinned())
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .authorId(notice.getAuthorId())
                .partId(notice.getPartId())
                .build();
    }
}