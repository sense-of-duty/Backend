package org.example.gdgpage.service.assignment;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.assignment.Assignment;
import org.example.gdgpage.dto.assignment.request.AssignmentCreateRequest;
import org.example.gdgpage.dto.assignment.response.AssignmentListResponse;
import org.example.gdgpage.dto.assignment.response.AssignmentResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.mapper.assignment.AssignmentMapper;
import org.example.gdgpage.repository.assignment.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    @Transactional
    public AssignmentResponse create(Long authorId, AssignmentCreateRequest request) {
        Assignment assignment = Assignment.create(
                authorId,
                request.title(),
                request.content(),
                request.dueAt(),
                request.parts());

        Assignment saved = assignmentRepository.save(assignment);
        return AssignmentMapper.toResponse(saved);
    }

    public List<AssignmentListResponse> getAll() {
        return assignmentRepository.findAll().stream()
                .map(AssignmentMapper::toListResponse)
                .toList();
    }

    public AssignmentResponse getOne(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_ASSIGNMENT));

        return AssignmentMapper.toResponse(assignment);
    }

    public Assignment getEntity(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_ASSIGNMENT));
    }
}
