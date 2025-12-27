package org.example.gdgpage.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.dto.oauth.request.OAuthLoginRequest;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    private final AuthService authService;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Value("${google.scope}")
    private String scope;

    @Value("${app.oauth.frontend-redirect-url:http://localhost:5173/oauth/callback}")
    private String frontendRedirectUrl;

    public String buildGoogleRedirectUrl(HttpServletResponse response) {
        String state = generateState();
        CookieUtil.setOAuthState(response, state);

        return UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("state", state)
                .build()
                .encode()
                .toUriString();
    }

    public String handleGoogleCallback(String code, String state, String stateCookie, HttpServletRequest request, HttpServletResponse response) {
        if (state == null || !state.equals(stateCookie)) {
            CookieUtil.clearOAuthState(response);
            throw new BadRequestException(ErrorMessage.INVALID_OAUTH_STATE);
        }
        CookieUtil.clearOAuthState(response);

        authService.oauthLogin(new OAuthLoginRequest("GOOGLE", code), request, response);

        return frontendRedirectUrl;
    }

    private String generateState() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
