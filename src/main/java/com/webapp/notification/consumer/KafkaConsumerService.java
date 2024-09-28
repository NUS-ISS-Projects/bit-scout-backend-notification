package com.webapp.notification.consumer;

import org.springframework.context.annotation.Profile;
import com.webapp.notification.service.ThresholdCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.notification.dto.PriceUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("!test") // Exclude this bean when the 'test' profile is active
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Stores the last price per token
    private final Map<String, Double> lastPriceMap = new ConcurrentHashMap<>();

    // Define a price change threshold (in percentage)
    private final double priceChangeThreshold = 0.1; // For example, 0.1%

    @Autowired
    private ThresholdCheckService thresholdCheckService;

@KafkaListener(topics = "price-updates", groupId = "notification-service")
public void consumePriceUpdate(String message) {
try {
// Deserialize the incoming message
PriceUpdateDto priceUpdateDto = objectMapper.readValue(message,
PriceUpdateDto.class);
// System.out.println("Received price update: " + priceUpdateDto);

// Get the last known price for the token
Double lastPrice = lastPriceMap.get(priceUpdateDto.getToken());

// Check if the price change is significant
if (lastPrice == null || hasSignificantChange(lastPrice,
priceUpdateDto.getPrice())) {
System.out.println("Price change for " + priceUpdateDto.getToken() + " is
significant, processing.");
// Update the last price in the map
lastPriceMap.put(priceUpdateDto.getToken(), priceUpdateDto.getPrice());

// Process the price update
thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);
} else {
// System.out.println("Price change for " + priceUpdateDto.getToken() + " is
insignificant, skipping.");
}
} catch (Exception e) {
logger.error("Failed to deserialize message: " + message, e);
}
}

    /**
     * Determines if the price change is significant based on a predefined
     * threshold.
     *
     * @param lastPrice the previous price of the token
     * @param newPrice  the new price of the token
     * @return true if the price change is significant, false otherwise
     */
    private boolean hasSignificantChange(double lastPrice, double newPrice) {
        double percentageChange = Math.abs((newPrice - lastPrice) / lastPrice) * 100;
        return percentageChange >= priceChangeThreshold;
    }
}
