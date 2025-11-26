package org.example.gdgpage.dto.freeboard.response;

import lombok.Getter;
import org.example.gdgpage.domain.freeboard.FreePost;

import java.time.LocalDateTime;

@Getter
public class FreePostListResponseDto {

    private Long id;
    private String title;
    private String previewContent;
    private String authorName;
    private Boolean isAnonymous;
    private Boolean isPinned;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;

    public FreePostListResponseDto(FreePost post) {

        this.id = post.getId();
        this.title = post.getTitle();
        this.authorName = post.getIsAnonymous()
                ? "익명"
                : post.getAuthor().getName();

        this.previewContent = createPreview(post.getContent());

        this.isAnonymous = post.getIsAnonymous();
        this.isPinned = post.getIsPinned();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.createdAt = post.getCreatedAt();
    }

    private String createPreview(String content) {
        return content.length() <= 70
                ? content
                : content.substring(0, 70) + "...";
    }
}
