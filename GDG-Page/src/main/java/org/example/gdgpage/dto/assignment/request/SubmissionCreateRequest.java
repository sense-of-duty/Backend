package org.example.gdgpage.dto.assignment.request;

import jakarta.validation.constraints.NotBlank;

public record SubmissionCreateRequest(
        @NotBlank
        String content,

        String attachmentUrl
) {}
