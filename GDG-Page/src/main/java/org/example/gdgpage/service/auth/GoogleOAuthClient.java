package org.example.gdgpage.service.auth;

import org.example.gdgpage.dto.oauth.response.GoogleTokenResponse;
import org.example.gdgpage.dto.oauth.response.GoogleUserInfoResponse;

public interface GoogleOAuthClient {
    GoogleTokenResponse exchangeCodeForToken(String code);
    GoogleUserInfoResponse getUserInfo(String accessToken);
}
