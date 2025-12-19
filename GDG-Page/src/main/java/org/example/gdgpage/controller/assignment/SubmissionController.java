package org.example.gdgpage.controller.assignment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.assignment.request.SubmissionCreateRequest;
import org.example.gdgpage.dto.assignment.response.SubmissionListResponse;
import org.example.gdgpage.dto.assignment.response.SubmissionResponse;
import org.example.gdgpage.service.assignment.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assignments/{assignmentId}/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "과제 제출(재제출 가능)", description = "활동 중인 유저만 가능. 기존 제출이 있으면 업데이트됨.")
    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(@PathVariable Long assignmentId,
                                                     @AuthenticationPrincipal AuthUser authUser,
                                                     @Valid @RequestBody SubmissionCreateRequest submissionCreateRequest) {
        SubmissionResponse response = submissionService.submitOrResubmit(assignmentId, authUser.id(), submissionCreateRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "제출 목록 조회(관리자 전용)", description = "관리자(core/organizer)만 제출 목록 조회 가능")
    @PreAuthorize("hasAnyRole('CORE','ORGANIZER')")
    @GetMapping
    public ResponseEntity<List<SubmissionListResponse>> getSubmissionsForAdmin(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsForAdmin(assignmentId));
    }

    @Operation(summary = "내 제출 조회", description = "로그인 유저 본인의 제출만 조회")
    @GetMapping("/me")
    public ResponseEntity<SubmissionResponse> getMySubmission(@PathVariable Long assignmentId,
                                                              @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(submissionService.getMySubmission(assignmentId, authUser.id()));
    }
}
