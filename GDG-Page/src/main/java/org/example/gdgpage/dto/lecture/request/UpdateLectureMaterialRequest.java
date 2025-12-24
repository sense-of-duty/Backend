package org.example.gdgpage.dto.lecture.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateLectureMaterialRequest(
        @Size(max = 80, message = "제목은 80자 이하로 입력해주세요.")
        String title,
        LocalDate publishedDate,
        String content
) {}
