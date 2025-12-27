package org.example.gdgpage.dto.notice.request.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record NoticeCommentCreateRequest(
        @NotBlank(message = "댓글을 작성해주세요.")
        String content,
        Long parentId
) {
}
