package org.example.gdgpage.dto.oauth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.gdgpage.domain.auth.Provider;

public record OAuthLoginRequest(
        @NotBlank
        String provider,

        @NotBlank
        String code
) {}
