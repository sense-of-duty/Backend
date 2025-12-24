package org.example.gdgpage.service.assignment;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.assignment.Assignment;
import org.example.gdgpage.domain.assignment.AssignmentSubmission;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.assignment.request.SubmissionCreateRequest;
import org.example.gdgpage.dto.assignment.response.SubmissionListResponse;
import org.example.gdgpage.dto.assignment.response.SubmissionResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.mapper.assignment.SubmissionMapper;
import org.example.gdgpage.repository.UserRepository;
import org.example.gdgpage.repository.assignment.AssignmentSubmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private final AssignmentService assignmentService;
    private final AssignmentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final AttachmentStorage attachmentStorage;
    private final Clock clock;

    @Transactional
    public SubmissionResponse submitOrResubmit(Long assignmentId, AuthUser authUser, SubmissionCreateRequest submissionCreateRequest, MultipartFile file) {
        Assignment assignment = assignmentService.getEntity(assignmentId);

        User user = userRepository.findById(authUser.id())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        if (user.isDeleted() || !user.isActive()) {
            throw new ForbiddenException(ErrorMessage.ONLY_ACTIVE_USER_CAN_SUBMIT);
        }

        boolean isAdmin = authUser.role().equals("ORGANIZER") || authUser.role().equals("CORE");

        if (!isAdmin) {
            if (assignment.getParts() != null && !assignment.getParts().isEmpty()) {
                PartType myPart = user.getPart();

                if (!assignment.getParts().contains(myPart)) {
                    throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
                }
            }
        }

        LocalDateTime now = LocalDateTime.now(clock);

        if (now.isAfter(assignment.getDueAt())) {
            throw new BadRequestException(ErrorMessage.ASSIGNMENT_DUE_PASSED);
        }

        String attachmentUrl = null;

        if (file != null && !file.isEmpty()) {
            attachmentUrl = attachmentStorage.uploadSubmissionAttachment(authUser.id(), file);
        }

        AssignmentSubmission submission = submissionRepository
                .findByAssignmentIdAndSubmitterId(assignmentId, authUser.id())
                .orElse(null);

        if (submission == null) {
            submission = AssignmentSubmission.create(assignmentId, authUser.id(), submissionCreateRequest.content(), attachmentUrl);
            submissionRepository.save(submission);
        } else {
            String newUrl = (attachmentUrl != null) ? attachmentUrl : submission.getAttachmentUrl();
            submission.resubmit(submissionCreateRequest.content(), newUrl);
        }

        return SubmissionMapper.toResponse(submission);
    }

    public Page<SubmissionListResponse> getSubmissionsForAdmin(Long assignmentId, Pageable pageable) {
        assignmentService.getEntity(assignmentId);

        return submissionRepository.findAllByAssignmentId(assignmentId, pageable)
                .map(SubmissionMapper::toListResponse);
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
