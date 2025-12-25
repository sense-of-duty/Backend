package org.example.gdgpage.service.notification;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.notification.Notification;
import org.example.gdgpage.domain.notification.NotificationType;
import org.example.gdgpage.dto.notification.NotificationResponseDto;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.ForbiddenException;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.repository.auth.UserRepository;
import org.example.gdgpage.repository.notification.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNotification(
            Long receiverId,
            NotificationType type,
            String message,
            Long targetId,
            String targetUrl
    ) {
        User receiver = getUser(receiverId);
        notificationRepository.save(Notification.create(receiver, type, message, targetId, targetUrl));
    }

    public Page<NotificationResponseDto> getMyNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByReceiverId(userId, pageable)
                .map(NotificationResponseDto::from);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_NOTIFICATION));

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new ForbiddenException(ErrorMessage.ACCESS_DENY);
        }

        if (!notification.isRead()) {
            notification.markAsRead();
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));
    }
}
