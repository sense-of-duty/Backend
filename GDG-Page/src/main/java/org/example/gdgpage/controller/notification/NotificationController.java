package org.example.gdgpage.controller.notification;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.domain.notification.Notification;
import org.example.gdgpage.dto.notification.NotificationResponseDto;
import org.example.gdgpage.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<NotificationResponseDto>> getMyNotifications(
            @AuthenticationPrincipal AuthUser authUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(authUser.id(), pageable)
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(notificationService.countUnread(authUser.id()));
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
