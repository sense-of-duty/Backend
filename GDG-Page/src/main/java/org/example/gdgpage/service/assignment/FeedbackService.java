package org.example.gdgpage.service.assignment;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.assignment.AssignmentSubmission;
import org.example.gdgpage.domain.assignment.SubmissionFeedback;
import org.example.gdgpage.dto.assignment.request.FeedbackCreateRequest;
import org.example.gdgpage.dto.assignment.response.FeedbackResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.mapper.assignment.FeedbackMapper;
import org.example.gdgpage.repository.assignment.SubmissionFeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final SubmissionService submissionService;
    private final SubmissionFeedbackRepository feedbackRepository;

    @Transactional
    public Long createFeedback(Long submissionId, Long authorId, FeedbackCreateRequest feedbackCreateRequest) {
        AssignmentSubmission submission = submissionService.getSubmissionEntity(submissionId);

        SubmissionFeedback feedback = SubmissionFeedback.create(
                submission.getId(),
                authorId,
                feedbackCreateRequest.content()
        );

        return feedbackRepository.save(feedback).getId();
    }

    public List<FeedbackResponse> getFeedbacks(Long submissionId) {
        submissionService.getSubmissionEntity(submissionId); // 존재 검증

        return feedbackRepository.findAllBySubmissionIdOrderByCreatedAtAsc(submissionId).stream()
                .map(FeedbackMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteFeedback(Long feedbackId, Long requesterId, boolean isAdmin) {
        SubmissionFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_FEEDBACK));

        if (!isAdmin && !feedback.getAuthorId().equals(requesterId)) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        feedbackRepository.delete(feedback);
    }
}
