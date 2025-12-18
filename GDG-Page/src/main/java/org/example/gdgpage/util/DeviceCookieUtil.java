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

    public static String getOrSetDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String existing = null;

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (Constants.DEVICE_ID.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    existing = c.getValue();
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

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return newId;
    }
}

