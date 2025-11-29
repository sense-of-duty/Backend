package org.example.gdgpage.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.dto.auth.request.LoginRequest;
import org.example.gdgpage.dto.auth.request.SignUpRequest;
import org.example.gdgpage.dto.auth.response.LoginResponse;
import org.example.gdgpage.dto.oauth.request.OAuthLoginRequest;
import org.example.gdgpage.dto.token.TokenDto;
import org.example.gdgpage.dto.token.request.RefreshTokenRequest;
import org.example.gdgpage.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest requestDTO) {
        LoginResponse response = authService.login(requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소셜 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/oauth/login")
    public ResponseEntity<LoginResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest requestDTO) {
        LoginResponse response = authService.oauthLogin(requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰 재발급 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@Valid @RequestBody RefreshTokenRequest request) {
        TokenDto tokenDto = authService.reissue(request);
        return ResponseEntity.ok(tokenDto);
    }
}
