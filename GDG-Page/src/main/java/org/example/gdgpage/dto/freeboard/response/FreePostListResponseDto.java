package org.example.gdgpage.dto.freeboard.response;

import lombok.Getter;
import org.example.gdgpage.domain.freeboard.FreePost;

import java.time.LocalDateTime;

@Getter
public class FreePostListResponseDto {

    private final Long id;
    private final String title;
    private final String previewContent;
    private final String authorName;
    private final Boolean isAnonymous;
    private final Boolean isPinned;
    private final Integer viewCount;
    private final Integer likeCount;
    private final Integer commentCount;
    private final LocalDateTime createdAt;

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
