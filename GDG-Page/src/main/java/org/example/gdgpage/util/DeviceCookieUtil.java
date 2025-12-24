package org.example.gdgpage.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeviceCookieUtil {

    private static final String PATH = "/";
    private static final String SAME_SITE = "Lax";

    public static String getOrSetDeviceId(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String existing = null;

        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (Constants.DEVICE_ID.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    existing = cookie.getValue();
                    break;
                }
            }
        }

        if (existing != null) {
            return existing;
        }

        String newId = UUID.randomUUID().toString();

        ResponseCookie cookie = ResponseCookie.from(Constants.DEVICE_ID, newId)
                .httpOnly(true)
                .secure(true)
                .path(PATH)
                .sameSite(SAME_SITE)
                .maxAge(Duration.ofDays(365))
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return newId;
    }
}

