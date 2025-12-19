package org.example.gdgpage.service.assignment;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.assignment.Assignment;
import org.example.gdgpage.domain.assignment.AssignmentSubmission;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.assignment.request.SubmissionCreateRequest;
import org.example.gdgpage.dto.assignment.response.SubmissionListResponse;
import org.example.gdgpage.dto.assignment.response.SubmissionResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.mapper.assignment.SubmissionMapper;
import org.example.gdgpage.repository.UserRepository;
import org.example.gdgpage.repository.assignment.AssignmentSubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private final AssignmentService assignmentService;
    private final AssignmentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubmissionResponse submitOrResubmit(Long assignmentId, Long submitterId, SubmissionCreateRequest submissionCreateRequest) {
        Assignment assignment = assignmentService.getEntity(assignmentId);

        User user = userRepository.findById(submitterId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        if (user.isDeleted() || !user.isActive()) {
            throw new BadRequestException(ErrorMessage.ONLY_ACTIVE_USER_CAN_SUBMIT);
        }

         if (LocalDateTime.now().isAfter(assignment.getDueAt())) {
             throw new BadRequestException(ErrorMessage.ASSIGNMENT_DUE_PASSED);
         }

        AssignmentSubmission submission = submissionRepository
                .findByAssignmentIdAndSubmitterId(assignmentId, submitterId)
                .orElse(null);

        if (submission == null) {
            submission = AssignmentSubmission.create(
                    assignmentId,
                    submitterId,
                    submissionCreateRequest.content(),
                    submissionCreateRequest.attachmentUrl()
            );
            submissionRepository.save(submission);
        } else {
            submission.resubmit(submissionCreateRequest.content(), submissionCreateRequest.attachmentUrl());
        }

        return SubmissionMapper.toResponse(submission);
    }

    public List<SubmissionListResponse> getSubmissionsForAdmin(Long assignmentId) {
        assignmentService.getEntity(assignmentId);

        return submissionRepository.findAllByAssignmentIdOrderByCreatedAtDesc(assignmentId).stream()
                .map(SubmissionMapper::toListResponse)
                .toList();
    }

    public SubmissionResponse getMySubmission(Long assignmentId, Long userId) {
        assignmentService.getEntity(assignmentId);

        AssignmentSubmission submission = submissionRepository.findByAssignmentIdAndSubmitterId(assignmentId, userId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_SUBMISSION));

        return SubmissionMapper.toResponse(submission);
    }

    public AssignmentSubmission getSubmissionEntity(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_SUBMISSION));
    }
}
