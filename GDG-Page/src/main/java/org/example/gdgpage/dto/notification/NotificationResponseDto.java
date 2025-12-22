package org.example.gdgpage.dto.notification;

import org.example.gdgpage.domain.notification.NotificationType;

public record NotificationResponseDto(
        Long id,
        NotificationType type,
        String title,
        String message,
        String dateLabel,
        boolean isRead,
        String targetUrl
) {}
