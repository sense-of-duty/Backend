package org.example.gdgpage.dto.assignment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AssignmentCreateRequest(
        @NotBlank
        String title,
        @NotBlank
        String content,
        @NotNull
        LocalDateTime dueAt,
        
        Long partId
) {}
