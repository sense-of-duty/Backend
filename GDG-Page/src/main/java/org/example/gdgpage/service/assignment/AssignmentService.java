package org.example.gdgpage.service.assignment;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.assignment.Assignment;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.assignment.request.AssignmentCreateRequest;
import org.example.gdgpage.dto.assignment.response.AssignmentListResponse;
import org.example.gdgpage.dto.assignment.response.AssignmentResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.mapper.assignment.AssignmentMapper;
import org.example.gdgpage.repository.UserRepository;
import org.example.gdgpage.repository.assignment.AssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final AttachmentStorage attachmentStorage;
    private final Clock clock;

    @Transactional
    public AssignmentResponse create(Long authorId, AssignmentCreateRequest request, MultipartFile file) {
        LocalDateTime now = LocalDateTime.now(clock);

        if (request.dueAt().isBefore(now) || request.dueAt().isEqual(now)) {
            throw new BadRequestException(ErrorMessage.ASSIGNMENT_DUE_PASSED);
        }

        String attachmentUrl = null;

        if (file != null && !file.isEmpty()) {
            attachmentUrl = attachmentStorage.uploadAssignmentAttachment(authorId, file);
        }

        Assignment assignment = Assignment.create(
                authorId,
                request.title(),
                request.content(),
                request.dueAt(),
                request.parts(),
                attachmentUrl
        );

        Assignment saved = assignmentRepository.save(assignment);
        return AssignmentMapper.toResponse(saved);
    }

    public Page<AssignmentListResponse> getAllVisible(AuthUser authUser, Pageable pageable) {
        if (authUser.role().equals("ORGANIZER") || authUser.role().equals("CORE")) {
            return assignmentRepository.findAll(pageable).map(AssignmentMapper::toListResponse);
        }

        PartType myPart = getMyPart(authUser.id());

        return assignmentRepository.findVisibleByPart(myPart, pageable)
                .map(AssignmentMapper::toListResponse);
    }

    public AssignmentResponse getOneVisible(Long assignmentId, AuthUser authUser) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_ASSIGNMENT));

        if (!canAccess(assignment, authUser)) {
            throw new ForbiddenException(ErrorMessage.NO_PERMISSION);
        }

        return AssignmentMapper.toResponse(assignment);
    }

    public Assignment getEntity(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_ASSIGNMENT));
    }

    private boolean canAccess(Assignment assignment, AuthUser authUser) {
        if (authUser.role().equals("ORGANIZER") || authUser.role().equals("CORE")) {
            return true;
        }

        if (assignment.getParts() == null || assignment.getParts().isEmpty()) {
            return true;
        }

        PartType myPart = getMyPart(authUser.id());

        return assignment.getParts().contains(myPart);
    }

    private PartType getMyPart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        return user.getPart();
    }
}
