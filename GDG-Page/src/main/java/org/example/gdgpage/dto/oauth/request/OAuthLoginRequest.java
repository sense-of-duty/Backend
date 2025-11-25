package org.example.gdgpage.dto.oauth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.auth.Provider;

@Getter
@NoArgsConstructor
public class OAuthLoginRequest {

    @NotNull(message = "Provider 는 필수입니다.")
    private Provider provider;

    @NotBlank(message = "인가 코드는 필수입니다.")
    private String authorizationCode;
}
