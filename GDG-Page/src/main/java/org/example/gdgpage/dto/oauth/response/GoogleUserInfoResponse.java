package org.example.gdgpage.dto.oauth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfoResponse (

    String id,
    String email,
    String name,

    @JsonProperty("verified_email")
    Boolean verifiedEmail,

    @JsonProperty("picture")
    String picture
) {}
