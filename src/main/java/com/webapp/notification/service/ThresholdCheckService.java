package com.webapp.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.dto.PriceUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ThresholdCheckService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private Firestore firestore;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RedisTemplate<String, List<Map<String, Object>>> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate; // Kafka producer for WebSocket broadcasting

    private static final String COLLECTION_NAME = "notifications";
    private static final String CACHE_KEY = "notificationDocuments";
    // private static final long CACHE_EXPIRATION = 60 * 2; // Cache expiration time in seconds
    // For demo use only, simulate no cache
    private static final long CACHE_EXPIRATION = 1; // Cache expiration time in seconds

    public void checkThresholdsForAllUsers(PriceUpdateDto priceUpdate) throws InterruptedException, ExecutionException {
        System.out.println("Starting threshold check for token: " + priceUpdate.getToken() + " with current price: " + priceUpdate.getPrice());

        // Check if notification documents are cached in Redis
        List<Map<String, Object>> cachedDocuments = redisTemplate.opsForValue().get(CACHE_KEY);

        if (cachedDocuments == null || cachedDocuments.isEmpty()) {
            // Cache is empty or expired, fetch from Firestore
            System.out.println("Cache expired or empty. Fetching from Firestore...");
            CollectionReference notificationsCollection = firestore.collection(COLLECTION_NAME);
            ApiFuture<QuerySnapshot> future = notificationsCollection.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            // Transform QueryDocumentSnapshot to a serializable format
            cachedDocuments = documents.stream()
                .map(QueryDocumentSnapshot::getData) // Extract data as Map<String, Object>
                .collect(Collectors.toList());

            // Cache the fetched documents in Redis with expiration
            redisTemplate.opsForValue().set(CACHE_KEY, cachedDocuments, CACHE_EXPIRATION, TimeUnit.SECONDS);
            System.out.println("Fetched " + documents.size() + " documents from Firestore and cached them in Redis.");
        } else {
            System.out.println("Using cached data from Redis.");
            System.out.println("Cached documents count: " + cachedDocuments.size());
        }

        // Proceed with the threshold check using the cached or freshly fetched documents
        processDocuments(cachedDocuments, priceUpdate);
    }

    private void processDocuments(List<Map<String, Object>> cachedDocuments, PriceUpdateDto priceUpdate) {
        for (Map<String, Object> document : cachedDocuments) {
            System.out.println("Processing userId: " + document.get("userId"));

            // Looking for the token within user's notifications
            String tokenKey = priceUpdate.getToken().toLowerCase();
            System.out.println("Looking for token: " + tokenKey + " in user's notifications.");

            if (document.containsKey(tokenKey)) {
                Map<String, Object> tokenData = (Map<String, Object>) document.get(tokenKey);
                double thresholdPrice = ((Number) tokenData.get("notificationValue")).doubleValue();
                String type = (String) tokenData.get("notificationType");

                System.out.println("Token found. Type: " + type + ", Threshold Price: " + thresholdPrice);

                boolean thresholdReached = checkThreshold(type, priceUpdate.getPrice(), thresholdPrice);
                System.out.println("Threshold reached for userId: " + document.get("userId") + " -> " + thresholdReached);

                if (thresholdReached) {
                    System.out.println("Notifying userId: " + document.get("userId") + " as threshold is met.");
                    notifyUser((String) document.get("userId"), priceUpdate.getToken(), type, thresholdPrice, priceUpdate.getPrice(), (String) tokenData.get("remarks"));
                } else {
                    System.out.println("No notification for userId: " + document.get("userId") + " as threshold is not met.");
                }
            } else {
                System.out.println("Token: " + tokenKey + " not found for userId: " + document.get("userId"));
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

        // Get pod hostname, to identify the pod that sent the notification
        // This is needed for the WebSocket broadcast so that the pod that sent the notification can be skipped
        String podId = System.getenv("HOSTNAME");  // Get pod hostname
        notificationDto.setPodId(podId);

        System.out.println("Sending notification to userId: " + userId + " for token: " + token + ", type: " + type + ", threshold: " + thresholdPrice + ", current price: " + currentPrice);
        notificationService.sendNotification(notificationDto);
        System.out.println("Notification sent to userId: " + userId);

        // Also send notification via Kafka to WebSocket broadcasting topic
        try {
            // Convert the notification DTO to a JSON string
            String message = objectMapper.writeValueAsString(notificationDto);

            // Send JSON message to Kafka topic
            kafkaTemplate.send("websocket-broadcasts", message);
        } catch (JsonProcessingException e) {
            // Handle JSON serialization error
            System.out.println("Error serializing notificationDto to JSON: " + e.getMessage());
        }
        System.out.println("Kafka WebSocket broadcast sent for userId: " + userId);
    }
}
