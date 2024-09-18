package com.webapp.notification.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.dto.PriceUpdateDto;
import com.webapp.notification.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ThresholdCheckService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private NotificationService notificationService;

    private static final String COLLECTION_NAME = "notifications";

    public void checkThresholdsForAllUsers(PriceUpdateDto priceUpdate) throws InterruptedException, ExecutionException {
        // Query Firestore for notifications by token
        CollectionReference notificationsCollection = firestore.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> future = notificationsCollection
                .whereEqualTo("token", priceUpdate.getToken())  // Firestore query
                .get();

        // Process the notifications
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Notification> notifications = documents.stream()
                .map(doc -> doc.toObject(Notification.class))
                .collect(Collectors.toList());

        // Check if the price condition is met
        for (Notification notification : notifications) {
            if (isThresholdReached(notification, priceUpdate.getPrice())) {
                notifyUser(notification);
            }
        }
    }

    // Check if the price condition has been reached
    public boolean isThresholdReached(Notification notification, Double currentPrice) {
        String type = notification.getNotificationType();
        double notificationValue = notification.getNotificationValue();

        switch (type.toLowerCase()) {
            case "price fall to":
                return currentPrice <= notificationValue;
            case "price rise to":
                return currentPrice >= notificationValue;
            default:
                return false;
        }
    }

    // Notify the user by sending the notification
    private void notifyUser(Notification notification) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(notification.getUserId());
        notificationDto.setRemarks("Price " + notification.getNotificationType() + " " +
                notification.getNotificationValue() + " for token " + notification.getToken());

        // Send the notification using the NotificationService
        notificationService.sendNotification(notificationDto);
    }
}
