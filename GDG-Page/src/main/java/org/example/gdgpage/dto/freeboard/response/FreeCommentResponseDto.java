package org.example.gdgpage.dto.freeboard.response;

import lombok.Getter;
import org.example.gdgpage.domain.freeboard.FreeComment;

import java.time.LocalDateTime;

@Getter
public class FreeCommentResponseDto {

    private Long id;
    private String content;
    private Boolean isAnonymous;
    private String authorName;
    private Integer likeCount;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FreeCommentResponseDto(FreeComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.isAnonymous = comment.getIsAnonymous();

        this.authorName = comment.getIsAnonymous()
                ? "익명"
                : comment.getAuthor().getName();

        this.likeCount = comment.getLikeCount();
        this.parentId = (comment.getParent() != null)
                ? comment.getParent().getId()
                : null;
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
