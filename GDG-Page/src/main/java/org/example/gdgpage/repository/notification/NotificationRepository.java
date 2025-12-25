package org.example.gdgpage.repository.notification;

import org.example.gdgpage.domain.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    long countByReceiverIdAndIsReadFalse(Long receiverId);
    Page<Notification> findByReceiverId(Long receiverId, Pageable pageable);
}
