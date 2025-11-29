package org.example.gdgpage.dto.oauth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse (

    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("expires_in")
    Integer expiresIn,

    @JsonProperty("scope")
    String scope,

    @JsonProperty("token_type")
    String tokenType
) {}
