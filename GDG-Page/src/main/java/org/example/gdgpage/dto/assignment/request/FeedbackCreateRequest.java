package org.example.gdgpage.dto.assignment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackCreateRequest(
        @NotBlank
        @Size(max = 2000)
        String content
) {}
