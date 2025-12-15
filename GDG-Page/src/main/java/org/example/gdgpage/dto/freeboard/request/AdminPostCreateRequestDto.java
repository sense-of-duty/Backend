package org.example.gdgpage.dto.freeboard.request;

public record AdminPostCreateRequestDto(
        String title,
        String content,
        Boolean isAnonymous,
        Boolean isPinned
) {}
