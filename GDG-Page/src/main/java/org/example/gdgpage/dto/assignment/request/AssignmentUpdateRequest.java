package org.example.gdgpage.dto.assignment.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.gdgpage.domain.auth.PartType;

import java.time.LocalDateTime;
import java.util.Set;

public record AssignmentUpdateRequest(
        @NotBlank
        @Size(max = 120)
        String title,

        @NotBlank
        @Size(max = 10000)
        String content,

        @NotNull
        @Future
        LocalDateTime dueAt,

        Set<PartType> parts
) {}
