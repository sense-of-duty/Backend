package org.example.gdgpage.domain.notification;

import jakarta.persistence.*;
import lombok.*;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(nullable = false, length = 255)
    private String message;

    private Long targetId;
    private String targetUrl;

    @Column(nullable = false)
    private boolean isRead;

    public void markAsRead() {
        this.isRead = true;
    }

    public static Notification create(User receiver, NotificationType type,
                                      String message, Long targetId, String targetUrl) {
        return Notification.builder()
                .receiver(receiver)
                .type(type)
                .message(message)
                .targetId(targetId)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();
    }
}
