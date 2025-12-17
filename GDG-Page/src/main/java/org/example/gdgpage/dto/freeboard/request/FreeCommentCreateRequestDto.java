package org.example.gdgpage.dto.freeboard.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FreeCommentCreateRequestDto(
        @NotBlank(message = "내용은 필수 입력값입니다.")
        String content,

        @NotNull(message = "익명 여부는 null일 수 없습니다.")
        Boolean isAnonymous,

        Long parentId
) {}
