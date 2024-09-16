package com.webapp.notification.consumer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentCaptor.forClass;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
        // Create a JSON string to simulate the message received from Kafka
        String jsonMessage = "{\"token\":\"BTC\",\"price\":60000.0}";

        // Call the method with the JSON string
        kafkaConsumerService.consumePriceUpdate(jsonMessage);

        // Capture the argument passed to the checkThresholdsForAllUsers method
        ArgumentCaptor<PriceUpdateDto> captor = forClass(PriceUpdateDto.class);
        verify(thresholdCheckService, times(1)).checkThresholdsForAllUsers(captor.capture());

        // Assert that the captured PriceUpdateDto has the expected values
        PriceUpdateDto capturedDto = captor.getValue();
        assertEquals("BTC", capturedDto.getToken());
        assertEquals(60000.0, capturedDto.getPrice());
    }
}
