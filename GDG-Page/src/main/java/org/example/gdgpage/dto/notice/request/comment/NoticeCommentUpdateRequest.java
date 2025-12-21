package org.example.gdgpage.dto.notice.request.comment;

import lombok.Builder;

@Builder
public record NoticeCommentUpdateRequest(
        String content,
        boolean isAnonymous
) {
}