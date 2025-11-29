package org.example.gdgpage.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest (

        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        String currentPassword,

        @NotBlank(message = "새로운 비밀번호는 필수입니다.")
        String newPassword,

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        String confirmNewPassword
) {}
