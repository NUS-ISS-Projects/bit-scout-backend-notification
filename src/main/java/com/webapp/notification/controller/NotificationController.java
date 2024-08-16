package com.webapp.notification.controller;

import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 3.3 Add Notification
    @PostMapping("/{userId}/add")
    public ResponseEntity<NotificationDto> addNotification(@PathVariable Long userId, @RequestBody NotificationDto notificationDto) {
        notificationDto.setUserId(userId);
        NotificationDto createdNotification = notificationService.createNotification(notificationDto);
        return ResponseEntity.ok(createdNotification);
    }

    // 3.4 Edit Notification
    @PutMapping("/{userId}/edit/{id}")
    public ResponseEntity<NotificationDto> editNotification(@PathVariable Long userId, @PathVariable Long id, @RequestBody NotificationDto notificationDto) {
        NotificationDto updatedNotification = notificationService.updateNotification(userId, id, notificationDto);
        return ResponseEntity.ok(updatedNotification);
    }

    // 3.5 Delete Notification
    @DeleteMapping("/{userId}/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long userId, @PathVariable Long id) {
        notificationService.deleteNotification(userId, id);
        return ResponseEntity.noContent().build();
    }

    // Get all notifications for user
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
}
