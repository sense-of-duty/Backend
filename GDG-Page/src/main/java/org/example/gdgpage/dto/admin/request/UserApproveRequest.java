package org.example.gdgpage.dto.admin.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserApproveRequest(
        @NotEmpty(message = "userIds 는 필수입니다.")
        List<Long> userIds
) {}
