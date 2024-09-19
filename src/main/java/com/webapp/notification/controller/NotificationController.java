package com.webapp.notification.controller;

import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.service.NotificationService;
import com.webapp.notification.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*") // Allow all origins
@Api(tags = "Notification Controller", description = "Endpoints for managing notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Add a new notification", notes = "Create a new notification for the user")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Notification added successfully"),
        @ApiResponse(code = 401, message = "Unauthorized")
    })
    @PostMapping("/add")
    public ResponseEntity<NotificationDto> addNotification(@RequestHeader("Authorization") String token,
            @RequestBody NotificationDto notificationDto)
            throws InterruptedException, ExecutionException {
        String userId = userService.validateTokenAndGetUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        notificationDto.setUserId(userId);
        NotificationDto createdNotification = notificationService.createNotification(notificationDto);
        return ResponseEntity.ok(createdNotification);
    }

    @ApiOperation(value = "Edit an existing notification", notes = "Update an existing notification")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Notification updated successfully"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Notification not found")
    })
    @PutMapping("/edit/{id}")
    public ResponseEntity<NotificationDto> editNotification(@RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody NotificationDto notificationDto)
            throws InterruptedException, ExecutionException {
        String userId = userService.validateTokenAndGetUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        NotificationDto updatedNotification = notificationService.updateNotification(userId, id, notificationDto);
        return ResponseEntity.ok(updatedNotification);
    }

    @ApiOperation(value = "Delete a notification", notes = "Delete a notification for the user")
    @ApiResponses({
        @ApiResponse(code = 204, message = "Notification deleted successfully"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Notification not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws InterruptedException, ExecutionException {
        String userId = userService.validateTokenAndGetUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        notificationService.deleteNotification(userId, id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Get all notifications", notes = "Retrieve all notifications for the user")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Notifications retrieved successfully"),
        @ApiResponse(code = 401, message = "Unauthorized")
    })
    @GetMapping("/list")
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestHeader("Authorization") String token)
            throws InterruptedException, ExecutionException {
        String userId = userService.validateTokenAndGetUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @ApiOperation(value = "Health check", notes = "Check if the service is running")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Service is running")
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        String podName = System.getenv("HOSTNAME");
        return ResponseEntity.ok("Notification Service is up and running on pod: " + podName);
    }
}
