package com.webapp.notification.consumer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.webapp.notification.dto.PriceUpdateDto;
import com.webapp.notification.service.NotificationService;
import com.webapp.notification.service.ThresholdCheckService;

class KafkaConsumerServiceTests {

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Mock
    private ThresholdCheckService thresholdCheckService;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsumePriceUpdate() {
        PriceUpdateDto priceUpdateDto = new PriceUpdateDto();
        priceUpdateDto.setToken("BTC");
        priceUpdateDto.setPrice(60000.0);
        
        ConsumerRecord<String, PriceUpdateDto> record = new ConsumerRecord<>("price-updates", 0, 0L, null, priceUpdateDto);
        
        kafkaConsumerService.consumePriceUpdate(record);
        
        verify(thresholdCheckService, times(1)).checkThresholdsForAllUsers(priceUpdateDto);
    }
}
