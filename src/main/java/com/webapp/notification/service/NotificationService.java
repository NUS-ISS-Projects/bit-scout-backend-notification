package com.webapp.notification.service;

import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;  // For WebSocket

    public NotificationDto createNotification(NotificationDto notificationDto) {
        Notification notification = new Notification();
        notification.setUserId(notificationDto.getUserId());
        notification.setToken(notificationDto.getToken());
        notification.setNotificationType(notificationDto.getNotificationType());
        notification.setNotificationValue(notificationDto.getNotificationValue());
        notification.setRemarks(notificationDto.getRemarks());

        Notification savedNotification = notificationRepository.save(notification);
        return mapToDto(savedNotification);
    }

    public NotificationDto updateNotification(Long userId, Long notificationId, NotificationDto notificationDto) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setNotificationType(notificationDto.getNotificationType());
        notification.setNotificationValue(notificationDto.getNotificationValue());
        notification.setRemarks(notificationDto.getRemarks());

        Notification updatedNotification = notificationRepository.save(notification);
        return mapToDto(updatedNotification);
    }

    public void deleteNotification(Long userId, Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public List<NotificationDto> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private NotificationDto mapToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setUserId(notification.getUserId());
        dto.setToken(notification.getToken());
        dto.setNotificationType(notification.getNotificationType());
        dto.setNotificationValue(notification.getNotificationValue());
        dto.setRemarks(notification.getRemarks());
        return dto;
    }

    public void sendNotification(NotificationDto notificationDto) {
        System.out.println("Notification sent to user " + notificationDto.getUserId() + ": " + notificationDto.getRemarks());
        // Send WebSocket Notification
        sendWebSocketNotification(notificationDto);
    }

    // Send real-time notification using WebSocket
    private void sendWebSocketNotification(NotificationDto notificationDto) {
        String destination = "/topic/notifications/" + notificationDto.getUserId();
        messagingTemplate.convertAndSend(destination, notificationDto);
        System.out.println("WebSocket Notification sent to user " + notificationDto.getUserId());
    }
}
