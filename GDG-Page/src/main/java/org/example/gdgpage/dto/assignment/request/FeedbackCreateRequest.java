package org.example.gdgpage.dto.assignment.request;

import jakarta.validation.constraints.NotBlank;

public record FeedbackCreateRequest(
        @NotBlank
        String content
) {}
