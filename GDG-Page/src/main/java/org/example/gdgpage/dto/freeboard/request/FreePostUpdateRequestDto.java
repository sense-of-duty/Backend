package org.example.gdgpage.dto.freeboard.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FreePostUpdateRequestDto(
        @NotBlank(message = "제목은 필수 입력값입니다.")
        String title,

        @NotBlank(message = "내용은 필수 입력값입니다.")
        String content,

        @NotNull(message = "pin 여부는 null일 수 없습니다.")
        Boolean isPinned
) {}
