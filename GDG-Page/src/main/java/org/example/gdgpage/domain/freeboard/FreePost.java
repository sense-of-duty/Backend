package org.example.gdgpage.domain.freeboard;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "free_posts")
public class FreePost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FreeComment> comments = new ArrayList<>();

    public void increaseViewCount() {
        this.viewCount++;
    }

    public FreePost(User author, String title, String content, Boolean isAnonymous, Boolean isPinned) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isPinned = isPinned;
    }

    public static FreePost create(User author, String title, String content, Boolean isAnonymous) {
        return new FreePost(
                author,
                title,
                content,
                isAnonymous,
                false
        );
    }

    public static FreePost createByAdmin(User author, String title, String content, Boolean isAnonymous, Boolean isPinned) {
        return new FreePost(
                author,
                title,
                content,
                isAnonymous,
                isPinned
        );
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateByAdmin(String title, String content, Boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
