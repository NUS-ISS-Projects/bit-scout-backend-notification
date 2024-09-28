package com.webapp.notification.controller;

import com.webapp.notification.dto.NotificationDto;

import com.webapp.notification.service.NotificationService;
import com.webapp.notification.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*") // Allow all origins
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // 3.3 Add or edit Notification
    @PostMapping("/add")
    public ResponseEntity<NotificationDto> addNotification(@RequestHeader("Authorization") String token,
            @RequestBody NotificationDto notificationDto)
            throws InterruptedException, ExecutionException {
        String userId = userService.validateTokenAndGetUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        notificationDto.setUserId(userId);
        NotificationDto createdNotification = notificationService.createOrUpdateNotification(notificationDto);
        return ResponseEntity.ok(createdNotification);
    }

    // 3.4 Edit Notification
    // @PutMapping("/edit/{id}")
    // public ResponseEntity<NotificationDto>
    // editNotification(@RequestHeader("Authorization") String token,
    // @PathVariable Long id,
    // @RequestBody NotificationDto notificationDto)
    // throws InterruptedException, ExecutionException {
    // String userId = userService.validateTokenAndGetUserId(token);
    // if (userId == null) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }
    // NotificationDto updatedNotification =
    // notificationService.createOrUpdateNotification(userId, id, notificationDto);
    // return ResponseEntity.ok(updatedNotification);
    // }

    // 3.5 Delete Notification
    @DeleteMapping("/delete/{coinName}")
    public ResponseEntity<String> deleteNotification(@RequestHeader("Authorization") String token,
            @PathVariable String coinName)
            throws InterruptedException, ExecutionException {
        String userId = userService.validateTokenAndGetUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // Call your deletion logic here, passing userId and coinName
            notificationService.deleteNotification(userId, coinName);
            return ResponseEntity.ok("Notification deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete notification: " + e.getMessage());
        }
    }

    // Get all notifications for user
    @GetMapping("/getUserList")
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestHeader("Authorization") String token)
            throws InterruptedException, ExecutionException {
        String userId = userService.validateTokenAndGetUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        String podName = System.getenv("HOSTNAME");
        return ResponseEntity.ok("Notification Service is up and running on pod: " + podName);
    }
}
