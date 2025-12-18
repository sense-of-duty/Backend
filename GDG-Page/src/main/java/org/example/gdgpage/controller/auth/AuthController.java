package org.example.gdgpage.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.dto.auth.request.LoginRequest;
import org.example.gdgpage.dto.auth.request.ResetPasswordRequest;
import org.example.gdgpage.dto.auth.request.SignUpRequest;
import org.example.gdgpage.dto.auth.response.EmailVerificationStatusResponse;
import org.example.gdgpage.dto.auth.response.LoginResponse;
import org.example.gdgpage.dto.auth.response.MessageResponse;
import org.example.gdgpage.dto.oauth.request.CompleteProfileRequest;
import org.example.gdgpage.dto.oauth.request.OAuthLoginRequest;
import org.example.gdgpage.dto.token.TokenDto;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "로컬 회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("해당 이메일로 인증 메일을 전송했습니다. 인증을 완료해주세요."));
    }

    @Operation(summary = "로그인", description = "로컬 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                               HttpServletRequest httpServletRequest,
                                               HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.login(loginRequest, httpServletRequest, httpServletResponse));
    }

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소셜 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/oauth/login")
    public ResponseEntity<LoginResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest oAuthLoginRequest,
                                                    HttpServletRequest httpServletRequest,
                                                    HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.oauthLogin(oAuthLoginRequest, httpServletRequest, httpServletResponse));
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰 재발급 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@CookieValue(name = Constants.REFRESH_TOKEN, required = false) String refreshToken,
                                            HttpServletRequest httpServletRequest,
                                            HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.reissue(refreshToken, httpServletRequest, httpServletResponse));
    }

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 만료시키고 로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰이 없거나 유효하지 않음")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = Constants.REFRESH_TOKEN, required = false) String refreshToken,
                                       HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse) {
        authService.logout(refreshToken, httpServletRequest, httpServletResponse);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "소셜 로그인 추가 정보 입력", description = "소셜 로그인 사용자의 프로필을 완성시킴")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 완성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음")
    })
    @PostMapping("/oauth/completeProfile")
    public ResponseEntity<UserResponse> completeProfile(@Valid @RequestBody CompleteProfileRequest completeProfileRequest) {
        UserResponse response = authService.completeProfile(completeProfileRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이메일 인증", description = "회원가입 후 이메일로 받은 토큰으로 이메일 인증")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
            @ApiResponse(responseCode = "400", description = "토큰이 유효하지 않음 또는 만료")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 재설정 메일 요청", description = "이메일로 비밀번호 재설정 링크 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 이메일")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> requestPasswordReset(@RequestParam("email") String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok(
                new MessageResponse("비밀번호 재설정 메일을 전송했습니다. 이메일을 확인해주세요.")
        );
    }

    @Operation(summary = "비밀번호 재설정", description = "메일로 받은 토큰으로 새 비밀번호 설정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "토큰이 유효하지 않음, 만료, 또는 비밀번호 불일치")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest.token(), resetPasswordRequest.newPassword(), resetPasswordRequest.confirmPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 메일 재발송", description = "이메일 미인증 계정에 인증 메일 재전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "이미 인증 완료된 이메일 또는 존재하지 않는 이메일")
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestParam("email") String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 상태 조회", description = "이메일 인증 완료 여부를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/verify-email/status")
    public ResponseEntity<EmailVerificationStatusResponse> emailVerificationStatus(@RequestParam("email") String email) {
        boolean verified = authService.getEmailVerificationStatus(email);
        return ResponseEntity.ok(new EmailVerificationStatusResponse(verified));
    }
}
