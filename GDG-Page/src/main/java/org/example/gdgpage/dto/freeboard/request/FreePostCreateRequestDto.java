package org.example.gdgpage.dto.freeboard.request;

public record FreePostCreateRequestDto(
        String title,
        String content,
        Boolean isAnonymous
) {}
