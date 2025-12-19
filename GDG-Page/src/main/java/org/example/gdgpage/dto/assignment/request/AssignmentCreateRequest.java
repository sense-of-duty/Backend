package org.example.gdgpage.dto.assignment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.gdgpage.domain.auth.PartType;

import java.time.LocalDateTime;
import java.util.Set;

public record AssignmentCreateRequest(
        @NotBlank
        String title,

        @NotBlank
        String content,
        @NotNull
        LocalDateTime dueAt,

        Set<PartType> parts
) {}
