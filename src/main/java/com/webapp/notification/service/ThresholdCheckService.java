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
        System.out.println("Checking thresholds for token: " + priceUpdate.getToken() + " with current price: " + priceUpdate.getPrice());

        // Query Firestore for notifications by token
        CollectionReference notificationsCollection = firestore.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> future = notificationsCollection
                .whereEqualTo("token", priceUpdate.getToken())  // Firestore query
                .get();

        try {
            // Process the notifications
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            System.out.println("Found " + documents.size() + " notifications for token " + priceUpdate.getToken());

            if (documents.isEmpty()) {
                System.out.println("No notifications found for token " + priceUpdate.getToken());
            }

            List<Notification> notifications = documents.stream()
                    .map(doc -> {
                        Notification notification = doc.toObject(Notification.class);
                        System.out.println("Processing notification: " + notification);
                        return notification;
                    })
                    .collect(Collectors.toList());

            // Check if the price condition is met
            for (Notification notification : notifications) {
                if (isThresholdReached(notification, priceUpdate.getPrice())) {
                    System.out.println("Threshold met for notification: " + notification);
                    notifyUser(notification);
                } else {
                    System.out.println("Threshold not met for notification: " + notification);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error fetching notifications from Firestore for token: " + priceUpdate.getToken());
            e.printStackTrace();
            throw e;  // rethrow the exception to maintain original behavior
        }
    }

    // Check if the price condition has been reached
    public boolean isThresholdReached(Notification notification, Double currentPrice) {
        String type = notification.getNotificationType();
        double notificationValue = notification.getNotificationValue();
        boolean thresholdReached = false;

        switch (type.toLowerCase()) {
            case "price fall to":
                thresholdReached = currentPrice <= notificationValue;
                System.out.println("Price fall to check: currentPrice=" + currentPrice + " <= notificationValue=" + notificationValue + " : " + thresholdReached);
                break;
            case "price rise to":
                thresholdReached = currentPrice >= notificationValue;
                System.out.println("Price rise to check: currentPrice=" + currentPrice + " >= notificationValue=" + notificationValue + " : " + thresholdReached);
                break;
            default:
                System.out.println("Unknown notification type: " + type);
                break;
        }
        return thresholdReached;
    }

    // Notify the user by sending the notification
    private void notifyUser(Notification notification) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(notification.getUserId());
        notificationDto.setRemarks("Price " + notification.getNotificationType() + " " +
                notification.getNotificationValue() + " for token " + notification.getToken());

        System.out.println("Sending notification to user: " + notificationDto.getUserId());
        // Send the notification using the NotificationService
        notificationService.sendNotification(notificationDto);
        System.out.println("Notification sent to user: " + notificationDto.getUserId());
    }
}
