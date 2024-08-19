package com.webapp.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.repository.NotificationRepository;
import com.webapp.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        notification = new Notification();
        notification.setId(1L);
        notification.setUserId(1L);
        notification.setToken("BTC");
        notification.setNotificationType("PriceAlert");
        notification.setNotificationValue(60000.0);
        notification.setRemarks("Test notification");

        notificationDto = new NotificationDto();
        notificationDto.setUserId(1L);
        notificationDto.setToken("BTC");
        notificationDto.setNotificationType("PriceAlert");
        notificationDto.setNotificationValue(60000.0);
        notificationDto.setRemarks("Test notification");
    }

    @Test
    void createNotificationTest() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationDto result = notificationService.createNotification(notificationDto);

        assertNotNull(result);
        assertEquals(notificationDto.getToken(), result.getToken());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void updateNotificationTest() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationDto updatedDto = notificationService.updateNotification(1L, 1L, notificationDto);

        assertNotNull(updatedDto);
        assertEquals(notificationDto.getRemarks(), updatedDto.getRemarks());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void deleteNotificationTest() {
        doNothing().when(notificationRepository).deleteById(1L);

        notificationService.deleteNotification(1L, 1L);

        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void getNotificationsByUserIdTest() {
        when(notificationRepository.findByUserId(1L)).thenReturn(Arrays.asList(notification));

        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(1L);

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        verify(notificationRepository, times(1)).findByUserId(1L);
    }

    @Test
    void sendNotificationTest() {
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(NotificationDto.class));

        notificationService.sendNotification(notificationDto);

        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(NotificationDto.class));
    }
}
