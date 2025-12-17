package org.example.gdgpage.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.dto.admin.request.UpdateUserRequest;
import org.example.gdgpage.dto.admin.request.UserApproveRequest;
import org.example.gdgpage.dto.admin.request.UserRejectRequest;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.service.admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "전체 유저 조회", description = "관리자가 모든 유저 목록을 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation(summary = "가입 요청 조회", description = "가입 대기/승인/반려 상태를 포함한 유저 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/requests")
    public ResponseEntity<List<UserResponse>> getSignupRequests() {
        return ResponseEntity.ok(adminService.getSignupRequests());
    }

    @Operation(summary = "유저 역할/파트 수정", description = "관리자가 유저의 역할과 파트를 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    @PatchMapping("/{userId}/role-part")
    public ResponseEntity<Void> updateUser(@PathVariable Long userId,
                                           @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        adminService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 강퇴", description = "유저를 강퇴(비활성화) 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "강퇴 처리 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    @PatchMapping("/{userId}/kick")
    public ResponseEntity<Void> expelUser(@PathVariable Long userId) {
        adminService.kickUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "가입 요청 승인(여러 명)", description = "체크박스로 선택한 유저들을 한 번에 승인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/approve")
    public ResponseEntity<Void> approveUsers(@Valid @RequestBody UserApproveRequest request) {
        adminService.approveUsers(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가입 요청 반려(여러 명)", description = "체크박스로 선택한 유저들을 한 번에 반려하고 공통 사유 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "반려 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음")
    })
    @PostMapping("/reject")
    public ResponseEntity<Void> rejectUsers(@Valid @RequestBody UserRejectRequest request) {
        adminService.rejectUsers(request);
        return ResponseEntity.ok().build();
    }
}
