package org.example.gdgpage.controller.attendance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.attendance.request.AttendanceCheckRequest;
import org.example.gdgpage.dto.attendance.response.ActiveSessionResponse;
import org.example.gdgpage.dto.attendance.response.AttendanceCheckResponse;
import org.example.gdgpage.dto.attendance.response.MyAttendanceResponse;
import org.example.gdgpage.service.attendance.AttendanceService;
import org.example.gdgpage.service.attendance.AttendanceSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AttendanceController {

    private final AttendanceSessionService sessionService;
    private final AttendanceService attendanceService;

    @Operation(summary = "활성화된 출석 조회", description = "활성된 출석이 있으면 입력창 표시, 없으면 204")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활성 출석 존재"),
            @ApiResponse(responseCode = "204", description = "활성 출석 없음"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/courses/{courseId}/attendance/active")
    public ResponseEntity<ActiveSessionResponse> getActive(@AuthenticationPrincipal AuthUser authUser) {
        ActiveSessionResponse active = attendanceService.getActive(authUser.id());

        if (active == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(active);
    }

    @Operation(summary = "출석 체크", description = "5분 내 번호 입력 성공 시 PRESENT 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출석 성공"),
            @ApiResponse(responseCode = "400", description = "세션 종료/만료/코드 불일치"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/weeks/{weekId}/attendance/check")
    public ResponseEntity<AttendanceCheckResponse> check(@Valid @RequestBody AttendanceCheckRequest request,
                                                         @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(attendanceService.checkAttendance(authUser.id(), request.code()));
    }

    @Operation(summary = "내 출석 현황 조회", description = "주차별 내 상태 반환. 기록 없으면 ABSENT로 간주")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/courses/{courseId}/attendance/my-attendance")
    public ResponseEntity<MyAttendanceResponse> myAttendance(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(attendanceService.getMyAttendance(authUser.id()));
    }
}
