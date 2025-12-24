package org.example.gdgpage.domain.assignment;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.auth.PartType; // 너 프로젝트 enum 재사용
import org.example.gdgpage.domain.common.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "assignments")
public class Assignment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime dueAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "assignment_parts",
            joinColumns = @JoinColumn(name = "assignment_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false, length = 10)
    @Builder.Default
    private Set<PartType> parts = new HashSet<>();

    @Column(nullable = false)
    private Long authorId;

    @Column(length = 500)
    private String attachmentUrl;

    public static Assignment create(Long authorId, String title, String content, LocalDateTime dueAt, Set<PartType> parts, String attachmentUrl) {
        return Assignment.builder()
                .authorId(authorId)
                .title(title)
                .content(content)
                .dueAt(dueAt)
                .parts(parts == null ? new HashSet<>() : new HashSet<>(parts))
                .attachmentUrl(attachmentUrl)
                .build();
    }

    public void update(String title, String content, LocalDateTime dueAt, Set<PartType> parts) {
        this.title = title;
        this.content = content;
        this.dueAt = dueAt;
        this.parts = (parts == null ? new HashSet<>() : new HashSet<>(parts));
    }
}
