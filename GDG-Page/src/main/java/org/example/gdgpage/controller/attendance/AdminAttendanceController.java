package org.example.gdgpage.controller.attendance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.attendance.request.AdminAttendanceUpdateRequest;
import org.example.gdgpage.dto.attendance.response.AdminSessionStartResponse;
import org.example.gdgpage.dto.attendance.response.WeekCreateResponse;
import org.example.gdgpage.service.attendance.AttendanceAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assign-admin")
public class AdminAttendanceController {

    private final AttendanceAdminService attendanceAdminService;

    @Operation(summary = "주차 생성", description = "첫 수업이면 1주차, 이후 누를 때마다 +1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 필요")
    })
    @PostMapping("/weeks")
    public ResponseEntity<WeekCreateResponse> createWeek(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(attendanceAdminService.createWeek(authUser.id()));
    }

    @Operation(summary = "출석 시작", description = "3자리 난수 생성, 5분 유효. 관리자에게만 code 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시작 성공"),
            @ApiResponse(responseCode = "400", description = "이미 진행 중인 출석이 존재"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 필요")
    })
    @PostMapping("/weeks/{weekId}/sessions")
    public ResponseEntity<AdminSessionStartResponse> startSession(@PathVariable Long weekId,
                                                                  @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(attendanceAdminService.startSession(authUser.id(), weekId));
    }

    @Operation(summary = "출석 종료", description = "즉시 출석 불가(코드 무효화)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "종료 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 필요")
    })
    @PostMapping("/sessions/{sessionId}/close")
    public ResponseEntity<Void> closeSession(@PathVariable Long sessionId,
                                             @AuthenticationPrincipal AuthUser authUser) {
        attendanceAdminService.closeSession(authUser.id(), sessionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출석 상태 수동 변경", description = "관리자가 주차별 유저 상태를 PRESENT/LATE/ABSENT로 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 필요")
    })
    @PatchMapping("/weeks/{weekId}/users/{userId}")
    public ResponseEntity<Void> adminUpdate(@PathVariable Long weekId,
                                            @PathVariable Long userId,
                                            @Valid @RequestBody AdminAttendanceUpdateRequest request,
                                            @AuthenticationPrincipal AuthUser authUser) {
        attendanceAdminService.updateStatus(authUser.id(), weekId, userId, request.status());
        return ResponseEntity.ok().build();
    }
}
