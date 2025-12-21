package org.example.gdgpage.dto.notice.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;

@Builder
public record NoticeCreateRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        boolean isPinned,

        @NotNull(message = "소속 파트를 선택해주세요.")
        PartType partId
) {
}