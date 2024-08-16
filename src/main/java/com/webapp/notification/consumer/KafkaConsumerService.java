package com.webapp.notification.consumer;

import com.webapp.notification.service.NotificationService;
import com.webapp.notification.service.ThresholdCheckService;
import com.webapp.notification.dto.PriceUpdateDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private ThresholdCheckService thresholdCheckService;

    @Autowired
    private NotificationService notificationService;

    // Kafka listener to consume messages from the "price-updates" topic
    @KafkaListener(topics = "price-updates", groupId = "notification-service")
    public void consumePriceUpdate(ConsumerRecord<String, PriceUpdateDto> record) {
        PriceUpdateDto priceUpdate = record.value();
        
        logger.info("Received price update for token {}: {}", priceUpdate.getToken(), priceUpdate.getPrice());
        
        // Process the price update and check against stored thresholds
        thresholdCheckService.checkThresholdsForAllUsers(priceUpdate);
    }
}
