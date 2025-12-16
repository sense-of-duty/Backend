package org.example.gdgpage.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserRejectRequest(

        @NotEmpty(message = "userIds 는 필수입니다.")
        List<Long> userIds,

        @NotBlank(message = "거절 사유는 필수입니다.")
        String reason
) {}
