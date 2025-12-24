package org.example.gdgpage.dto.assignment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmissionCreateRequest(
        @NotBlank
        @Size(max = 10000)
        String content
) {}
