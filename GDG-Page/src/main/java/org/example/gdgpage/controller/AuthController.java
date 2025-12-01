package org.example.gdgpage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.dto.auth.request.LoginRequest;
import org.example.gdgpage.dto.auth.request.SignUpRequest;
import org.example.gdgpage.dto.auth.response.LoginResponse;
import org.example.gdgpage.dto.auth.response.UserResponse;
import org.example.gdgpage.dto.oauth.request.CompleteProfileRequest;
import org.example.gdgpage.dto.oauth.request.OAuthLoginRequest;
import org.example.gdgpage.dto.token.TokenDto;
import org.example.gdgpage.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인", description = "로컬 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        LoginResponse loginResponse = authService.login(loginRequest, httpServletResponse);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소셜 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/oauth/login")
    public ResponseEntity<LoginResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest oAuthLoginRequest, HttpServletResponse httpServletResponse) {
        LoginResponse loginResponse = authService.oauthLogin(oAuthLoginRequest, httpServletResponse);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰 재발급 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@CookieValue(name = Constants.REFRESH_TOKEN, required = false) String refreshToken, HttpServletResponse httpServletResponse) {
        TokenDto tokenDto = authService.reissue(refreshToken, httpServletResponse);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(
            summary = "로그아웃", description = "리프레시 토큰을 만료시키고 로그아웃"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰이 없거나 유효하지 않음")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = Constants.REFRESH_TOKEN, required = false) String refreshToken, HttpServletResponse httpServletResponse) {
        authService.logout(refreshToken, httpServletResponse);
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
    public ResponseEntity<UserResponse> completeProfile(@Valid @RequestBody CompleteProfileRequest request) {
        UserResponse response = authService.completeProfile(request);
        return ResponseEntity.ok(response);
    }
}
