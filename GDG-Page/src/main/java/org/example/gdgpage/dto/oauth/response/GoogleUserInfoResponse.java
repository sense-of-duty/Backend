package org.example.gdgpage.dto.oauth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GoogleUserInfoResponse {

    private String id;
    private String email;
    private String name;

    @JsonProperty("verified_email")
    private Boolean verifiedEmail;

    @JsonProperty("picture")
    private String picture;
}
