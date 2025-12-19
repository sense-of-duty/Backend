package org.example.gdgpage.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    ALREADY_EXIST_EMAIL("이미 존재하는 이메일입니다."),
    WRONG_EMAIL_INPUT("이메일 또는 비밀번호가 올바르지 않습니다."),
    WRONG_PASSWORD_INPUT("이메일 또는 비밀번호가 올바르지 않습니다."),
    NOT_EXIST_USER("존재하지 않는 사용자입니다."),
    NOT_EXIST_POST("존재하지 않는 글입니다."),
    NEED_TO_LOGIN("로그인이 필요한 서비스입니다."),
    NO_PERMISSION("수정/삭제 권한이 없습니다."),
    NOT_EXIST_COMMENT("존재하지 않는 댓글입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    NO_REFRESH_TOKEN_IN_LOGIN("리프레시 토큰은 로그인에 사용할 수 없습니다."),
    NEED_TO_AUTHORIZE("인증이 필요합니다."),
    ACCESS_DENY("접근 권한이 없습니다."),
    EMAIL_ALREADY_REGISTERED_WITH_OTHER_PROVIDER("이미 존재하는 이메일입니다."),
    OAUTH_CODE_EXCHANGE_FAILED("구글 인가 코드를 액세스 토큰 교환에 실패했습니다."),
    OAUTH_PROFILE_FETCH_FAILED("구글 사용자 프로필을 불러오지 못했습니다."),
    OAUTH_EMAIL_NOT_VERIFIED("구글 계정 이메일이 검증되지 않았습니다."),
    WRONG_CHECK_PASSWORD("비밀번호가 일치하지 않습니다."),
    ALREADY_EXIST_PHONE("이미 존재하는 번호입니다."),
    ALREADY_PROFILE_COMPLETED("이미 프로필 작성이 완료된 유저입니다."),
    WRONG_CURRENT_PASSWORD("비밀번호가 일치하지 않습니다."),
    OAUTH_ACCOUNT_CANNOT_CHANGE_PASSWORD("소셜 로그인 유저는 비밀번호 변경이 불가합니다."),
    INVALID_PROFILE_IMAGE("유효하지 않은 프로필 이미지입니다."),
    INVALID_EMAIL_VERIFICATION_TOKEN("유효하지 않은 링크입니다."),
    EXPIRED_EMAIL_VERIFICATION_TOKEN("만료된 링크입니다."),
    EMAIL_ALREADY_VERIFIED("이미 이메일 인증이 완료된 계정입니다."),
    EMAIL_NOT_VERIFIED("이메일 인증이 완료되지 않았습니다."),
    INVALID_PASSWORD_RESET_TOKEN("유효하지 않은 링크입니다."),
    EXPIRED_PASSWORD_RESET_TOKEN("만료된 링크입니다."),
    INVALID_REQUEST("반려 사유를 입력해주세요."),
    DELETED_USER("삭제된 유저입니다."),
    NOT_EXIST_ASSIGNMENT("존재하지 않는 과제입니다."),
    ONLY_ACTIVE_USER_CAN_SUBMIT("현 기수 멤버만 제출 가능합니다."),
    ASSIGNMENT_DUE_PASSED("과제 제출 기한이 지났습니다."),
    NOT_EXIST_SUBMISSION("아직 과제를 제출하지 않았습니다."),
    NOT_EXIST_FEEDBACK("존재하지 않는 피드백입니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
