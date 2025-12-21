package org.example.gdgpage.dto.notice.request.post;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

@Builder
public record NoticeUpdateRequest(
        String title,
        String content,
        boolean isPinned,
        PartType partId
) {
}