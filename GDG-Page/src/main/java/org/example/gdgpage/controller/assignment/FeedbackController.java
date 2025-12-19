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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/submissions/{submissionId}/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 댓글 작성", description = "로그인한 유저라면 누구나 작성 가능")
    @PostMapping
    public ResponseEntity<Long> create(@PathVariable Long submissionId,
                                       @AuthenticationPrincipal AuthUser authUser,
                                       @Valid @RequestBody FeedbackCreateRequest feedbackCreateRequest) {
        Long id = feedbackService.createFeedback(submissionId, authUser.id(), feedbackCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @Operation(summary = "피드백 댓글 목록 조회", description = "로그인한 유저라면 누구나 조회 가능")
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAll(@PathVariable Long submissionId) {
        return ResponseEntity.ok(feedbackService.getFeedbacks(submissionId));
    }
}
