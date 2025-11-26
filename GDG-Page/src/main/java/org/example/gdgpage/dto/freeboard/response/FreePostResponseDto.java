package org.example.gdgpage.dto.freeboard.response;

import lombok.Getter;
import org.example.gdgpage.domain.freeboard.FreePost;

import java.time.LocalDateTime;

@Getter
public class FreePostResponseDto {

    private Long id;
    private String title;
    private String content;
    private Boolean isAnonymous;
    private Boolean isPinned;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FreePostResponseDto(FreePost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.isAnonymous = post.getIsAnonymous();
        this.isPinned = post.getIsPinned();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.authorName = post.getAuthor().getName();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
