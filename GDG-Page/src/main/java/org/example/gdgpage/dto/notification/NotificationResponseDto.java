package org.example.gdgpage.dto.notification;

import lombok.Builder;
import lombok.Getter;
import org.example.gdgpage.domain.notification.Notification;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {

    private Long id;
    private String type;
    private String message;
    private Long targetId;
    private String targetUrl;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .message(notification.getMessage())
                .targetId(notification.getTargetId())
                .targetUrl(notification.getTargetUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
