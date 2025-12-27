package org.example.gdgpage.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.service.auth.GoogleOAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/auth/oauth/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String url = googleOAuthService.buildGoogleRedirectUrl(response);
        response.sendRedirect(url);
    }

    @GetMapping("/oauth2/callback/google")
    public void callback(@RequestParam("code") String code,
                         @RequestParam(value = "state", required = false) String state,
                         @CookieValue(name = "OAUTH_STATE", required = false) String stateCookie,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String redirectUrl = googleOAuthService.handleGoogleCallback(code, state, stateCookie, request, response);
        response.sendRedirect(redirectUrl);
    }
}
