package org.example.gdgpage.repository.notification;

import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);
    long countByReceiverAndIsReadFalse(User receiver);
}
