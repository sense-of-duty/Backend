package org.example.gdgpage.dto.notice.request.comment;

import lombok.Builder;

@Builder
public record NoticeCommentCreateRequest(
        String content,
        boolean isAnonymous,
        Long parentId,
        String postType
) {
}