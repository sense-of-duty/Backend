package org.example.gdgpage.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.dto.oauth.response.GoogleTokenResponse;
import org.example.gdgpage.dto.oauth.response.GoogleUserInfoResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClientImpl implements GoogleOAuthClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Override
    public GoogleTokenResponse exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, GoogleTokenResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BadRequestException(ErrorMessage.OAUTH_CODE_EXCHANGE_FAILED);
            }

            return response.getBody();
        } catch (RestClientException e) {
            throw new BadRequestException(ErrorMessage.OAUTH_CODE_EXCHANGE_FAILED);
        }
    }

    @Override
    public GoogleUserInfoResponse getUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

        try {
            ResponseEntity<GoogleUserInfoResponse> exchanged = restTemplate.exchange(USERINFO_URL, HttpMethod.GET, httpEntity, GoogleUserInfoResponse.class);

            if (!exchanged.getStatusCode().is2xxSuccessful() || exchanged.getBody() == null) {
                throw new BadRequestException(ErrorMessage.OAUTH_PROFILE_FETCH_FAILED);
            }

            return exchanged.getBody();
        } catch (RestClientException e) {
            throw new BadRequestException(ErrorMessage.OAUTH_PROFILE_FETCH_FAILED);
        }
    }
}
