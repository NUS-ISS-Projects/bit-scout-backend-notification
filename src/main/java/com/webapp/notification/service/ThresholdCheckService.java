package com.webapp.notification.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.dto.PriceUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ThresholdCheckService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private NotificationService notificationService;

    private static final String COLLECTION_NAME = "notifications";

    public void checkThresholdsForAllUsers(PriceUpdateDto priceUpdate) throws InterruptedException, ExecutionException {
        System.out.println("Starting threshold check for token: " + priceUpdate.getToken() + " with current price: " + priceUpdate.getPrice());

        // Fetching all notifications from Firestore
        CollectionReference notificationsCollection = firestore.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> future = notificationsCollection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        System.out.println("Total users with notifications: " + documents.size());

        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> data = document.getData();
            System.out.println("Processing userId: " + document.getId());

            // Looking for the token within user's notifications
            String tokenKey = priceUpdate.getToken().toLowerCase() + "usdt";
            System.out.println("Looking for token: " + tokenKey + " in user's notifications.");

            if (data.containsKey(tokenKey)) {
                Map<String, Object> tokenData = (Map<String, Object>) data.get(tokenKey);
                double thresholdPrice = ((Number) tokenData.get("notificationValue")).doubleValue();
                String type = (String) tokenData.get("notificationType");

                System.out.println("Token found. Type: " + type + ", Threshold Price: " + thresholdPrice);

                boolean thresholdReached = checkThreshold(type, priceUpdate.getPrice(), thresholdPrice);
                System.out.println("Threshold reached for userId: " + document.getId() + " -> " + thresholdReached);

                if (thresholdReached) {
                    System.out.println("Notifying userId: " + document.getId() + " as threshold is met.");
                    notifyUser(document.getId(), priceUpdate.getToken(), type, thresholdPrice, priceUpdate.getPrice(), (String) tokenData.get("remarks"));
                } else {
                    System.out.println("No notification for userId: " + document.getId() + " as threshold is not met.");
                }
            } else {
                System.out.println("Token: " + tokenKey + " not found for userId: " + document.getId());
            }
        }
        System.out.println("Threshold check completed for all users.");
    }

    private boolean checkThreshold(String type, double currentPrice, double thresholdPrice) {
        System.out.println("Checking threshold. Type: " + type + ", Current Price: " + currentPrice + ", Threshold Price: " + thresholdPrice);

        switch (type) {
            case "price rise to":
                boolean priceRise = currentPrice >= thresholdPrice;
                System.out.println("Price rise check: " + priceRise);
                return priceRise;
            case "price fall to":
                boolean priceFall = currentPrice <= thresholdPrice;
                System.out.println("Price fall check: " + priceFall);
                return priceFall;
            default:
                System.out.println("Unknown threshold type: " + type);
                return false;
        }
    }

    private void notifyUser(String userId, String token, String type, double thresholdPrice, double currentPrice, String remarks) {
        System.out.println("Preparing notification for userId: " + userId + ", Token: " + token);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setToken(token);
        notificationDto.setNotificationType(type);
        notificationDto.setNotificationValue(thresholdPrice);
        notificationDto.setCurrentPrice(currentPrice); // Set current price
        notificationDto.setRemarks(remarks);

        System.out.println("Sending notification to userId: " + userId + " for token: " + token + ", type: " + type + ", threshold: " + thresholdPrice + ", current price: " + currentPrice);
        notificationService.sendNotification(notificationDto);
        System.out.println("Notification sent to userId: " + userId);
    }
}
