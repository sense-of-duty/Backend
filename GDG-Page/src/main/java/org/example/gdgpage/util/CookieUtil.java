package org.example.gdgpage.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtil {

    private static final String PATH = "/";
    private static final String SAME_SITE = "Lax";

    public static void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(Constants.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(PATH)
                .sameSite(SAME_SITE)
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(Constants.REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(true)
                .path(PATH)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
