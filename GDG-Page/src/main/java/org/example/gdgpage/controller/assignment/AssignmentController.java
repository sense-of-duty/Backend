package org.example.gdgpage.controller.assignment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.assignment.request.AssignmentCreateRequest;
import org.example.gdgpage.dto.assignment.request.AssignmentUpdateRequest;
import org.example.gdgpage.dto.assignment.response.AssignmentListResponse;
import org.example.gdgpage.dto.assignment.response.AssignmentResponse;
import org.example.gdgpage.service.assignment.AssignmentService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Operation(summary = "과제 생성", description = "관리자(core/organizer)만 과제 생성 가능")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CORE','ORGANIZER')")
    public ResponseEntity<AssignmentResponse> create(@AuthenticationPrincipal AuthUser authUser,
                                                     @Valid @RequestPart("request") AssignmentCreateRequest request,
                                                     @RequestPart(value = "file", required = false) MultipartFile file) {
        AssignmentResponse response = assignmentService.create(authUser.id(), request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "과제 목록 조회", description = "로그인 유저 과제 목록 조회")
    @GetMapping
    public ResponseEntity<Page<AssignmentListResponse>> getAll(@AuthenticationPrincipal AuthUser authUser,
                                                               @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(assignmentService.getAllVisible(authUser, pageable));
    }

    @Operation(summary = "과제 상세 조회", description = "로그인 유저 과제 상세 조회")
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getOne(@PathVariable Long assignmentId,
                                                     @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(assignmentService.getOneVisible(assignmentId, authUser));
    }

    @Operation(summary = "과제 수정", description = "관리자(core/organizer)만 수정 가능. 파일 교체 가능.")
    @PutMapping(value = "/{assignmentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentResponse> update(@PathVariable Long assignmentId,
                                                     @AuthenticationPrincipal AuthUser authUser,
                                                     @Valid @RequestPart("request") AssignmentUpdateRequest request,
                                                     @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(assignmentService.update(assignmentId, authUser, request, file));
    }

    @Operation(summary = "과제 삭제", description = "관리자(core/organizer)만 삭제 가능(소프트 삭제)")
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> delete(@PathVariable Long assignmentId, @AuthenticationPrincipal AuthUser authUser) {
        assignmentService.deleteAssignment(assignmentId, authUser);
        return ResponseEntity.noContent().build();
    }
}
