package org.example.gdgpage.domain.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.common.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "assignments", indexes = {
        @Index(name = "idx_assignment_dueAt", columnList = "dueAt"),
        @Index(name = "idx_assignment_partId", columnList = "partId")
})
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

    @Column
    private Long partId;

    @Column(nullable = false)
    private Long authorId;

    public static Assignment create(Long authorId, String title, String content, LocalDateTime dueAt, Long partId) {
        return Assignment.builder()
                .authorId(authorId)
                .title(title)
                .content(content)
                .dueAt(dueAt)
                .partId(partId)
                .build();
    }

    public void update(String title, String content, LocalDateTime dueAt, Long partId) {
        this.title = title;
        this.content = content;
        this.dueAt = dueAt;
        this.partId = partId;
    }
}
