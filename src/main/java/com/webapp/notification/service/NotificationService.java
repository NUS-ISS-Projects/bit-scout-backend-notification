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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
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

        System.out.println("Starting createOrUpdateNotification for user: " + userId + " with token: " + token);

        // Firestore reference
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId);

        // Retrieve the document from Firestore
        System.out.println("Fetching document for userId: " + userId);
        ApiFuture<DocumentSnapshot> future = document.get();
        DocumentSnapshot documentSnapshot = future.get();
        System.out.println("Document found: " + documentSnapshot.exists());

        String notificationType = "notificationType";
        String notificationValue = "notificationValue";
        String remarks = "remarks";
        Map<String, Object> notificationData;
        if (documentSnapshot.exists()) {
            // Document exists, update or add the coin information
            notificationData = documentSnapshot.getData();
            System.out.println("Updating existing document for token: " + token);

            // Check if token already exists and update its values
            if (notificationData.containsKey(token)) {
                Map<String, Object> tokenData = (Map<String, Object>) notificationData.get(token);
                System.out.println("Token data exists, updating values for token: " + token);
                tokenData.put(notificationType, notificationDto.getNotificationType());
                tokenData.put(notificationValue, notificationDto.getNotificationValue());
                tokenData.put(remarks, notificationDto.getRemarks());
            } else {
                System.out.println("Adding new token data for token: " + token);
                // Add new token data
                Map<String, Object> tokenData = new HashMap<>();
                tokenData.put(notificationType, notificationDto.getNotificationType());
                tokenData.put(notificationValue, notificationDto.getNotificationValue());
                tokenData.put(remarks, notificationDto.getRemarks());
                notificationData.put(token, tokenData);
            }
        } else {
            System.out.println("Document does not exist, creating new document for userId: " + userId);
            // Document does not exist, create new document with the notification data
            notificationData = new HashMap<>();
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put(notificationType, notificationDto.getNotificationType());
            tokenData.put(notificationValue, notificationDto.getNotificationValue());
            tokenData.put(remarks, notificationDto.getRemarks());
            notificationData.put(token, tokenData);
            notificationData.put("userId", userId);
        }

        // Save the updated or new document to Firestore
        System.out.println("Saving document to Firestore for userId: " + userId);
        WriteResult result = document.set(notificationData).get();
        System.out.println("Document saved successfully, update time: " + result.getUpdateTime());

        // Update or create notification in the repository
        List<Notification> existingNotifications = notificationRepository.findByUserId(userId);
        Notification notification;
        if (!existingNotifications.isEmpty()) {
            System.out.println("Existing notification found for userId: " + userId + ", updating it.");
            // Update the first existing notification
            notification = existingNotifications.get(0);
            notification.setNotificationType(notificationDto.getNotificationType());
            notification.setNotificationValue(notificationDto.getNotificationValue());
            notification.setRemarks(notificationDto.getRemarks());
            notification.setToken(notificationDto.getToken());
        } else {
            System.out.println("No existing notification found for userId: " + userId + ", creating new notification.");
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
        System.out.println("Notification saved to repository for userId: " + userId);

        return mapToDto(savedNotification);
    }

    public void deleteNotification(String userId, String coinName)
            throws InterruptedException, ExecutionException {
        System.out.println("Deleting notification for token: " + coinName + " for userId: " + userId);

        // Reference to the notifications collection
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId);

        // Retrieve the document from Firestore
        ApiFuture<DocumentSnapshot> future = document.get();
        DocumentSnapshot documentSnapshot = future.get();

        // Check if the document exists
        if (documentSnapshot.exists()) {
            Map<String, Object> notificationData = documentSnapshot.getData();

            // Check if the token exists in the document
            if (notificationData.containsKey(coinName)) {
                // Remove the token entry from the notification data
                notificationData.remove(coinName);
                // Save the updated document back to Firestore
                WriteResult result = document.set(notificationData).get();
                System.out.println(
                        "Deleted notification for token: " + coinName + ", update time: " + result.getUpdateTime());
            } else {
                System.out.println("No notification found for token: " + coinName);
            }
        } else {
            System.out.println("Document does not exist for userId: " + userId);
        }
    }

    public List<NotificationDto> getNotificationsByUserId(String userId)
            throws InterruptedException, ExecutionException {
        System.out.println("Fetching notifications for userId: " + userId);
        DocumentReference document = firestore.collection(COLLECTION_NAME).document(userId);

        // Fetch the user document
        DocumentSnapshot documentSnapshot = document.get().get();

        if (documentSnapshot.exists()) {
            System.out.println("Document found for userId: " + userId);
            Map<String, Object> data = documentSnapshot.getData();
            List<NotificationDto> notifications = new ArrayList<>();

            // Iterate through the data entries to find notifications
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String token = entry.getKey(); // This is the notification key (e.g., "btcjy", "ethusdt")
                System.out.println("Processing notification for token: " + token);

                // Check if the entry value is a map (notification data)
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> notificationData = (Map<String, Object>) entry.getValue();

                    // Create NotificationDto and set its properties
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setUserId(userId); // Set userId from the parameter
                    notificationDto.setToken(token); // Use the entry key as the token
                    notificationDto.setNotificationType((String) notificationData.get("notificationType"));
                    notificationDto.setNotificationValue(
                            ((Number) notificationData.get("notificationValue")).doubleValue()); // Convert to Double
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
        if (notification == null) {
            return null; // Handle null case
        }
        System.out.println("Mapping Notification entity to DTO for userId: " + notification.getUserId());
        NotificationDto dto = new NotificationDto();
        dto.setUserId(notification.getUserId());
        dto.setToken(notification.getToken());
        dto.setNotificationType(notification.getNotificationType());
        dto.setNotificationValue(notification.getNotificationValue());
        dto.setRemarks(notification.getRemarks());
        return dto;
    }

    public void sendNotification(NotificationDto notificationDto) {
        System.out.println("Sending notification to user " + notificationDto.getUserId());
        System.out.println("Notification details: Token=" + notificationDto.getToken() +
                ", Type=" + notificationDto.getNotificationType() +
                ", Value=" + notificationDto.getNotificationValue() +
                ", Remarks=" + notificationDto.getRemarks());

        // Send WebSocket Notification
        sendWebSocketNotification(notificationDto);
    }

    // Send real-time notification using WebSocket
    private void sendWebSocketNotification(NotificationDto notificationDto) {
        String destination = "/topic/notifications/" + notificationDto.getUserId();
        messagingTemplate.convertAndSend(destination, notificationDto);
        System.out.println("WebSocket Notification sent to user " + notificationDto.getUserId() + " at destination "
                + destination);
    }
}
