package org.example.gdgpage.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String CONTENT_TYPE = "application/json;charset=utf-8";
    public static final String MESSAGE_INTRO = "{\"message\":\"";
    public static final String MESSAGE_OUTRO = "\"}";
    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String DEFAULT_PROFILE_IMAGE_URL = "https://gdgpage-growthon.s3.ap-northeast-2.amazonaws.com/profiles/default_img.webp";
}
