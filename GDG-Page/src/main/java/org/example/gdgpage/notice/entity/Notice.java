package org.example.gdgpage.notice.entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.common.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notices") //db 네임 이름은 user보니까 users이길래 이것도 s를 붙엿음
public class Notice extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private Long authorId;


    private Long partId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    @Column(nullable = false)
    private boolean isPinned = false;


    @Column(nullable = false)
    private int viewCount = 0;


    private LocalDateTime deletedAt;

    // 생성자 (Builder 패턴 사용 추천)
    @Builder
    public Notice(Long authorId, Long partId, String title, String content, boolean isPinned) {
        this.authorId = authorId;
        this.partId = partId;
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;

    }


    public void update(String title, String content, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;

    }


    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }


    public void increaseViewCount() {
        this.viewCount++;
    }
}