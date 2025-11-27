package org.example.gdgpage.dto.oauth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.gdgpage.domain.auth.Provider;

public record OAuthLoginRequest (

    @NotNull(message = "Provider 는 필수입니다.")
    Provider provider,

    @NotBlank(message = "인가 코드는 필수입니다.")
    String authorizationCode
) {}
