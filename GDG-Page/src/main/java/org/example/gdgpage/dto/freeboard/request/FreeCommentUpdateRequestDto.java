package org.example.gdgpage.dto.freeboard.request;

import jakarta.validation.constraints.NotBlank;

public record FreeCommentUpdateRequestDto(
        @NotBlank(message = "내용은 필수 입력값입니다.")
        String content
) {}
