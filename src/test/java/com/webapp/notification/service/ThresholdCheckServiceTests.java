package com.webapp.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.dto.PriceUpdateDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.repository.NotificationRepository;
import com.webapp.notification.service.NotificationService;
import com.webapp.notification.service.ThresholdCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

class ThresholdCheckServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ThresholdCheckService thresholdCheckService;

    private PriceUpdateDto priceUpdateDto;
    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        priceUpdateDto = new PriceUpdateDto();
        priceUpdateDto.setToken("BTC");
        priceUpdateDto.setPrice(60000.0);

        notification = new Notification();
        notification.setUserId(1L);
        notification.setToken("BTC");
        notification.setNotificationType("price fall to");
        notification.setNotificationValue(62000.0);
    }

    @Test
    void checkThresholdsForAllUsers_PriceFallReached_ShouldNotifyUser() {
        when(notificationRepository.findByToken("BTC")).thenReturn(Arrays.asList(notification));

        thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);

        verify(notificationService, times(1)).sendNotification(any(NotificationDto.class));
    }

    @Test
    void checkThresholdsForAllUsers_PriceRiseReached_ShouldNotNotifyUser() {
        notification.setNotificationType("price rise to");
        notification.setNotificationValue(65000.0);

        when(notificationRepository.findByToken("BTC")).thenReturn(Arrays.asList(notification));

        thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);

        verify(notificationService, times(0)).sendNotification(any(NotificationDto.class));
    }

    @Test
    void checkThresholdsForAllUsers_NoNotifications_ShouldNotNotifyUser() {
        when(notificationRepository.findByToken("BTC")).thenReturn(Arrays.asList());

        thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);

        verify(notificationService, times(0)).sendNotification(any(NotificationDto.class));
    }

    @Test
    void isThresholdReached_PriceFallReached_ShouldReturnTrue() {
        boolean result = thresholdCheckService.isThresholdReached(notification, 60000.0);
        assertTrue(result);
    }

    @Test
    void isThresholdReached_PriceRiseNotReached_ShouldReturnFalse() {
        notification.setNotificationType("price rise to");
        notification.setNotificationValue(65000.0);

        boolean result = thresholdCheckService.isThresholdReached(notification, 60000.0);
        assertFalse(result);
    }
}
