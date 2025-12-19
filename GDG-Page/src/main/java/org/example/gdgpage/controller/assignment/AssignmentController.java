package org.example.gdgpage.controller.assignment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.assignment.request.AssignmentCreateRequest;
import org.example.gdgpage.dto.assignment.response.AssignmentListResponse;
import org.example.gdgpage.dto.assignment.response.AssignmentResponse;
import org.example.gdgpage.service.assignment.AssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Operation(summary = "과제 생성", description = "관리자(core/organizer)만 과제 생성 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('CORE','ORGANIZER')")
    public ResponseEntity<AssignmentResponse> create(@AuthenticationPrincipal AuthUser authUser,
                                                     @Valid @RequestBody AssignmentCreateRequest request) {
        AssignmentResponse response = assignmentService.create(authUser.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "과제 목록 조회", description = "로그인 유저 과제 목록 조회")
    @GetMapping
    public ResponseEntity<List<AssignmentListResponse>> getAll() {
        return ResponseEntity.ok(assignmentService.getAll());
    }

    @Operation(summary = "과제 상세 조회", description = "로그인 유저 과제 상세 조회")
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getOne(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getOne(assignmentId));
    }
}
