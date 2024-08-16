package com.webapp.notification.service;

import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.dto.PriceUpdateDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ThresholdCheckService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String WATCHLIST_SERVICE_URL = "http://watchlist-service:8888/api/watchlist/";

    // Accept price updates from Kafka and check thresholds for each user
    public void checkThresholdsForAllUsers(PriceUpdateDto priceUpdate) {
        List<Notification> notifications = notificationRepository.findByToken(priceUpdate.getToken());
        
        // For each notification, check if the price condition is met
        for (Notification notification : notifications) {
            if (isThresholdReached(notification, priceUpdate.getPrice())) {
                // Fetch the list of users for the token
                List<Long> userIds = getUsersForToken(notification.getToken());
                notifyUsers(userIds, notification);
            }
        }
    }

    private boolean isThresholdReached(Notification notification, Double currentPrice) {
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

    // Fetch users from the Watchlist Service
    private List<Long> getUsersForToken(String token) {
        String url = WATCHLIST_SERVICE_URL + "tokens/" + token;
        Long[] userIds = restTemplate.getForObject(url, Long[].class);
        return List.of(userIds);
    }

    private void notifyUsers(List<Long> userIds, Notification notification) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setRemarks("Price " + notification.getNotificationType() + " " +
                notification.getNotificationValue() + " for token " + notification.getToken());

        for (Long userId : userIds) {
            notificationDto.setUserId(userId);
            notificationService.sendNotification(notificationDto);
        }
    }
}
