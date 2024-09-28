package com.webapp.notification.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
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

    public NotificationDto createOrUpdateNotification(NotificationDto notificationDto)
            throws InterruptedException, ExecutionException {

        String userId = notificationDto.getUserId();
        String token = notificationDto.getToken();

        // Firestore reference
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId);

        // Retrieve the document from Firestore
        ApiFuture<DocumentSnapshot> future = document.get();
        DocumentSnapshot documentSnapshot = future.get();

        Map<String, Object> notificationData;
        if (documentSnapshot.exists()) {
            // Document exists, update or add the coin information
            notificationData = documentSnapshot.getData();

            // Check if token already exists and update its values
            if (notificationData.containsKey(token)) {
                Map<String, Object> tokenData = (Map<String, Object>) notificationData.get(token);
                tokenData.put("notificationType", notificationDto.getNotificationType());
                tokenData.put("notificationValue", notificationDto.getNotificationValue());
                tokenData.put("remarks", notificationDto.getRemarks());
            } else {
                // Add new token data
                Map<String, Object> tokenData = new HashMap<>();
                tokenData.put("notificationType", notificationDto.getNotificationType());
                tokenData.put("notificationValue", notificationDto.getNotificationValue());
                tokenData.put("remarks", notificationDto.getRemarks());
                notificationData.put(token, tokenData);
            }
        } else {
            // Document does not exist, create new document with the notification data
            notificationData = new HashMap<>();
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("notificationType", notificationDto.getNotificationType());
            tokenData.put("notificationValue", notificationDto.getNotificationValue());
            tokenData.put("remarks", notificationDto.getRemarks());
            notificationData.put(token, tokenData);
            notificationData.put("userId", userId);
        }

        // Save the updated or new document to Firestore
        WriteResult result = document.set(notificationData).get();

        // Update or create notification in the repository
        List<Notification> existingNotifications = notificationRepository.findByUserId(userId);
        Notification notification;
        if (!existingNotifications.isEmpty()) {
            // Update the first existing notification
            notification = existingNotifications.get(0);
            notification.setNotificationType(notificationDto.getNotificationType());
            notification.setNotificationValue(notificationDto.getNotificationValue());
            notification.setRemarks(notificationDto.getRemarks());
            notification.setToken(notificationDto.getToken());
        } else {
            // Create new notification object
            notification = new Notification();
            notification.setUserId(userId);
            notification.setToken(token);
            notification.setNotificationType(notificationDto.getNotificationType());
            notification.setNotificationValue(notificationDto.getNotificationValue());
            notification.setRemarks(notificationDto.getRemarks());
        }

        // Save the notification to the repository
        Notification savedNotification = notificationRepository.save(notification);
        return mapToDto(savedNotification);
    }

    public void deleteNotification(String userId, Long notificationId) throws InterruptedException, ExecutionException {
        // Delete the notification from Firestore
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId);
        document.delete().get();

        notificationRepository.deleteById(notificationId);
    }

    public List<NotificationDto> getNotificationsByUserId(String userId)
            throws InterruptedException, ExecutionException {
        DocumentReference document = firestore.collection(COLLECTION_NAME).document(userId);

        // Fetch the user document
        DocumentSnapshot documentSnapshot = document.get().get();

        if (documentSnapshot.exists()) {
            Map<String, Object> data = documentSnapshot.getData();
            List<NotificationDto> notifications = new ArrayList<>();

            // Iterate through the data entries to find notifications
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String token = entry.getKey(); // This is the notification key (e.g., "btcjy", "ethusdt")

                // Check if the entry value is a map (notification data)
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> notificationData = (Map<String, Object>) entry.getValue();

                    // Create NotificationDto and set its properties
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setUserId(userId); // Set userId from the parameter
                    notificationDto.setToken(token); // Use the entry key as the token
                    notificationDto.setNotificationType((String) notificationData.get("notificationType"));
                    notificationDto
                            .setNotificationValue(((Number) notificationData.get("notificationValue")).doubleValue()); // Convert
                                                                                                                       // to
                                                                                                                       // Double
                    notificationDto.setRemarks((String) notificationData.get("remarks"));

                    // Add the notification to the list
                    notifications.add(notificationDto);
                }
            }

            return notifications;
        } else {
            System.out.println("No document found for userId: " + userId);
            return Collections.emptyList();
        }
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
