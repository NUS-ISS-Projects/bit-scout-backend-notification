package com.webapp.notification.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.repository.NotificationRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;

@Service
public class NotificationService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // For WebSocket

    private static final String COLLECTION_NAME = "notifications";

    public NotificationDto createNotification(NotificationDto notificationDto)
            throws InterruptedException, ExecutionException {

        // Save notification to Firestore
        String userId = notificationDto.getUserId().toString();
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId);
        WriteResult result = document.set(notificationDto).get();

        Notification notification = new Notification();
        notification.setUserId(notificationDto.getUserId());
        notification.setToken(notificationDto.getToken());
        notification.setNotificationType(notificationDto.getNotificationType());
        notification.setNotificationValue(notificationDto.getNotificationValue());
        notification.setRemarks(notificationDto.getRemarks());

        Notification savedNotification = notificationRepository.save(notification);
        return mapToDto(savedNotification);
    }

    public NotificationDto updateNotification(Long userId, Long notificationId, NotificationDto notificationDto)
            throws InterruptedException, ExecutionException {

        // Update the notification in Firestore
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId.toString());
        WriteResult result = document.set(notificationDto).get();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setNotificationType(notificationDto.getNotificationType());
        notification.setNotificationValue(notificationDto.getNotificationValue());
        notification.setRemarks(notificationDto.getRemarks());

        Notification updatedNotification = notificationRepository.save(notification);
        return mapToDto(updatedNotification);
    }

    public void deleteNotification(Long userId, Long notificationId) throws InterruptedException, ExecutionException {
        // Delete the notification from Firestore
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId.toString());
        document.delete().get();

        notificationRepository.deleteById(notificationId);
    }

    public List<NotificationDto> getNotificationsByUserId(Long userId) throws InterruptedException, ExecutionException {

        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        return notifications.get().get().getDocuments().stream()
                .map(doc -> doc.toObject(NotificationDto.class))
                .collect(Collectors.toList());
        // List<Notification> notifications =
        // notificationRepository.findByUserId(userId);
        // return
        // notifications.stream().map(this::mapToDto).collect(Collectors.toList());
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
        System.out.println(
                "Notification sent to user " + notificationDto.getUserId() + ": " + notificationDto.getRemarks());
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
