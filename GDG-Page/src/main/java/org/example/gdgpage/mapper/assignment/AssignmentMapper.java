package org.example.gdgpage.mapper.assignment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.assignment.Assignment;
import org.example.gdgpage.dto.assignment.response.AssignmentListResponse;
import org.example.gdgpage.dto.assignment.response.AssignmentResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssignmentMapper {

    public static AssignmentResponse toResponse(Assignment assignment) {
        if (assignment == null) {
            return null;
        }

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .content(assignment.getContent())
                .dueAt(assignment.getDueAt())
                .partId(assignment.getPartId())
                .authorId(assignment.getAuthorId())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }

    public static AssignmentListResponse toListResponse(Assignment assignment) {
        if (assignment == null) {
            return null;
        }

        return AssignmentListResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .dueAt(assignment.getDueAt())
                .partId(assignment.getPartId())
                .authorId(assignment.getAuthorId())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}
