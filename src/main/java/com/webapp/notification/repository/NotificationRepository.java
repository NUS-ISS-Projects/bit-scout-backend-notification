package com.webapp.notification.repository;

import com.webapp.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByToken(String token);
}
