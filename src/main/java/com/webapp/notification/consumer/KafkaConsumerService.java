package com.webapp.notification.consumer;

import org.springframework.context.annotation.Profile;
import com.webapp.notification.service.NotificationService;
import com.webapp.notification.service.ThresholdCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.notification.dto.PriceUpdateDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Profile("!test") // Exclude this bean when the 'test' profile is active
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ThresholdCheckService thresholdCheckService;

    @KafkaListener(topics = "price-updates", groupId = "notification-service")
    public void consumePriceUpdate(String message) {
        try {
            // Manually deserialize JSON string to PriceUpdateDto
            PriceUpdateDto priceUpdateDto = objectMapper.readValue(message, PriceUpdateDto.class);
            System.out.println("Received price update: " + priceUpdateDto);
            
            // Process the price update and check thresholds
            thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);
        } catch (Exception e) {
            logger.error("Failed to deserialize message: " + message, e);
        }
    }
}
