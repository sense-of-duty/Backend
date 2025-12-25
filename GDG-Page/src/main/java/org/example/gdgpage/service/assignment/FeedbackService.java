package org.example.gdgpage.service.assignment;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.assignment.AssignmentSubmission;
import org.example.gdgpage.domain.assignment.SubmissionFeedback;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.dto.assignment.request.FeedbackCreateRequest;
import org.example.gdgpage.dto.assignment.request.FeedbackUpdateRequest;
import org.example.gdgpage.dto.assignment.response.FeedbackResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.mapper.assignment.FeedbackMapper;
import org.example.gdgpage.repository.assignment.SubmissionFeedbackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final SubmissionService submissionService;
    private final SubmissionFeedbackRepository feedbackRepository;

    @Transactional
    public FeedbackResponse createFeedback(Long submissionId, Long authorId, FeedbackCreateRequest feedbackCreateRequest) {
        AssignmentSubmission submission = submissionService.getSubmissionEntity(submissionId);

        SubmissionFeedback feedback = SubmissionFeedback.create(
                submission.getId(),
                authorId,
                feedbackCreateRequest.content()
        );

        SubmissionFeedback saved = feedbackRepository.save(feedback);
        return FeedbackMapper.toResponse(saved);
    }

    public Page<FeedbackResponse> getFeedbacks(Long submissionId, Pageable pageable) {
        submissionService.getSubmissionEntity(submissionId);

        return feedbackRepository.findAllBySubmissionId(submissionId, pageable)
                .map(FeedbackMapper::toResponse);
    }

    @Transactional
    public FeedbackResponse updateFeedback(Long submissionId, Long feedbackId, AuthUser authUser, FeedbackUpdateRequest request) {
        SubmissionFeedback feedback = feedbackRepository.findByIdAndSubmissionId(feedbackId, submissionId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_FEEDBACK));

        boolean isAdmin = authUser.role().equals("ORGANIZER") || authUser.role().equals("CORE");
        boolean isAuthor = feedback.getAuthorId().equals(authUser.id());

        if (!isAdmin && !isAuthor) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        feedback.updateContent(request.content());

        return FeedbackMapper.toResponse(feedback);
    }

    @Transactional
    public void deleteFeedback(Long submissionId, Long feedbackId, AuthUser authUser) {
        SubmissionFeedback feedback = feedbackRepository.findByIdAndSubmissionId(feedbackId, submissionId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_FEEDBACK));

        boolean isAuthor = feedback.getAuthorId().equals(authUser.id());
        boolean isAdmin = authUser.role().equals("ORGANIZER") || authUser.role().equals("CORE");

        if (!isAdmin && !isAuthor) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        feedbackRepository.delete(feedback);
    }
}
