package org.example.gdgpage.dto.freeboard.response;

import lombok.Getter;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.domain.freeboard.FreePost;

import java.time.LocalDateTime;

@Getter
public class FreePostResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final Boolean isAnonymous;
    private final Boolean isPinned;
    private final Integer viewCount;
    private final Integer likeCount;
    private final Integer commentCount;
    private final String authorName;
    private final String imageUrl;
    private final String profileImageUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public FreePostResponseDto(FreePost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.isAnonymous = post.getIsAnonymous();

        this.authorName = post.getIsAnonymous()
                ? "익명"
                : post.getAuthor().getName();

        this.profileImageUrl = isAnonymous
                ? Constants.DEFAULT_PROFILE_IMAGE_URL
                : post.getAuthor().getProfileImageUrl();

        this.isPinned = post.getIsPinned();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.imageUrl = post.getImageUrl();
        this.commentCount = post.getCommentCount();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
