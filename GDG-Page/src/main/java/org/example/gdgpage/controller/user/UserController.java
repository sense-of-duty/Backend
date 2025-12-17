package org.example.gdgpage.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.user.request.UpdatePasswordRequest;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 프로필을 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/mypage")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal AuthUser authUser) {
        UserResponse response = userService.getMyProfile(authUser.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 검증 후 새 비밀번호로 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PatchMapping("/mypage/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal AuthUser authUser,
                                               @Valid @RequestBody UpdatePasswordRequest request) {
        userService.changePassword(authUser.id(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 이미지 변경", description = "마이페이지에서 프로필 이미지를 업로드하여 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PatchMapping(value = "/mypage/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateProfileImage(@AuthenticationPrincipal AuthUser authUser,
                                                           @RequestPart("file") MultipartFile file) {
        UserResponse response = userService.updateProfileImage(authUser.id(), file);
        return ResponseEntity.ok(response);
    }
}
