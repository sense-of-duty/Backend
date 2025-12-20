package org.example.gdgpage.dto.freeboard.response;

import lombok.Getter;
import org.example.gdgpage.domain.freeboard.FreeComment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class FreeCommentResponseDto {

    private final Long id;
    private final String content;
    private final Boolean isAnonymous;
    private final String authorName;
    private final Integer likeCount;
    private final Long parentId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final List<FreeCommentResponseDto> children = new ArrayList<>();

    public FreeCommentResponseDto(FreeComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.isAnonymous = comment.getIsAnonymous();
        this.authorName = comment.getIsAnonymous()
                ? "익명"
                : comment.getAuthor().getName();

        this.likeCount = comment.getLikeCount();
        this.parentId = comment.getParent() != null
                ? comment.getParent().getId()
                : null;

        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }

    public void addChild(FreeCommentResponseDto child) {
        this.children.add(child);
    }
}
