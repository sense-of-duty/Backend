package org.example.gdgpage.controller.notification;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.domain.notification.Notification;
import org.example.gdgpage.dto.notification.NotificationResponseDto;
import org.example.gdgpage.service.notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(notificationService.getMyNotifications(authUser.id()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        long count = notificationService.countUnread(authUser.id());
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        notificationService.markAsRead(notificationId, authUser.id());
        return ResponseEntity.ok().build();
    }

}
