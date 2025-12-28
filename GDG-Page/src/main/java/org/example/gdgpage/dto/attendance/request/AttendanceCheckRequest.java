package org.example.gdgpage.dto.attendance.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AttendanceCheckRequest(
        @NotBlank
        @Pattern(regexp = "\\d{3}", message = "3자리 숫자만 입력 가능합니다.")
        String code
) {}
