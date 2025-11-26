package org.example.gdgpage.dto.freeboard.request;

public record FreeCommentCreateRequestDto(
        String content,
        Boolean isAnonymous,
        Long parentId
) {}
