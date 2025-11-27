package org.example.gdgpage.dto.token.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest (

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    String refreshToken
) {}
