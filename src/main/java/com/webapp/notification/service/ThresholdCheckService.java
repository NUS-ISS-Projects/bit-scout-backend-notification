package com.webapp.notification.service;

import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.dto.PriceUpdateDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThresholdCheckService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    // Accept price updates from Kafka and check thresholds for each user
    public void checkThresholdsForAllUsers(PriceUpdateDto priceUpdate) {
        List<Notification> notifications = notificationRepository.findByToken(priceUpdate.getToken());
        
        // For each notification, check if the price condition is met
        for (Notification notification : notifications) {
            if (isThresholdReached(notification, priceUpdate.getPrice())) {
                notifyUser(notification);
            }
        }
    }

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

    // Directly notify the user from the notification entity
    private void notifyUser(Notification notification) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(notification.getUserId());  // Use userId from the notification entity
        notificationDto.setRemarks("Price " + notification.getNotificationType() + " " +
                notification.getNotificationValue() + " for token " + notification.getToken());

        notificationService.sendNotification(notificationDto);
    }
}
