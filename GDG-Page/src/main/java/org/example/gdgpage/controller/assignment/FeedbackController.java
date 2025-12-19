package org.example.gdgpage.controller.assignment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.assignment.request.FeedbackCreateRequest;
import org.example.gdgpage.dto.assignment.response.FeedbackResponse;
import org.example.gdgpage.service.assignment.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 댓글 작성", description = "로그인한 유저라면 누구나 작성 가능")
    @PostMapping
    public ResponseEntity<FeedbackResponse> create(@PathVariable Long submissionId,
                                                   @AuthenticationPrincipal AuthUser authUser,
                                                   @Valid @RequestBody FeedbackCreateRequest feedbackCreateRequest) {
        FeedbackResponse response = feedbackService.createFeedback(submissionId, authUser.id(), feedbackCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "피드백 댓글 목록 조회", description = "로그인한 유저라면 누구나 조회 가능")
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAll(@PathVariable Long submissionId) {
        return ResponseEntity.ok(feedbackService.getFeedbacks(submissionId));
    }

    @Operation(summary = "피드백 댓글 삭제", description = "본인 작성자이거나 관리자(CORE/ORGANIZER)일 때만 삭제 가능")
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> delete(@PathVariable Long submissionId,
                                       @PathVariable Long feedbackId,
                                       @AuthenticationPrincipal AuthUser authUser) {

        boolean isAdmin = authUser.role().equals("CORE") || authUser.role().equals("ORGANIZER");

        feedbackService.deleteFeedback(submissionId, feedbackId, authUser.id(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}
