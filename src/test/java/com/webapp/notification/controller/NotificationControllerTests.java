package com.webapp.notification.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;
import java.util.List;

import com.webapp.notification.controller.NotificationController;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class NotificationControllerTests {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNotification() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setToken("testToken");
        notificationDto.setNotificationType("testType");

        when(notificationService.createNotification(any(NotificationDto.class))).thenReturn(notificationDto);

        ResponseEntity<NotificationDto> response = notificationController.addNotification(1L, notificationDto);

        verify(notificationService, times(1)).createNotification(any(NotificationDto.class));
        assert response.getBody().getToken().equals("testToken");
    }

    @Test
    void testEditNotification() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setToken("updatedToken");

        when(notificationService.updateNotification(eq(1L), eq(1L), any(NotificationDto.class)))
                .thenReturn(notificationDto);

        ResponseEntity<NotificationDto> response = notificationController.editNotification(1L, 1L, notificationDto);

        verify(notificationService, times(1)).updateNotification(eq(1L), eq(1L), any(NotificationDto.class));
        assert response.getBody().getToken().equals("updatedToken");
    }

    @Test
    void testDeleteNotification() {
        ResponseEntity<Void> response = notificationController.deleteNotification(1L, 1L);

        verify(notificationService, times(1)).deleteNotification(1L, 1L);
        assert response.getStatusCode().is2xxSuccessful();
    }

    @Test
    void testGetNotifications() {
        NotificationDto notificationDto1 = new NotificationDto();
        notificationDto1.setToken("testToken1");

        NotificationDto notificationDto2 = new NotificationDto();
        notificationDto2.setToken("testToken2");

        List<NotificationDto> notifications = Arrays.asList(notificationDto1, notificationDto2);

        when(notificationService.getNotificationsByUserId(1L)).thenReturn(notifications);

        ResponseEntity<List<NotificationDto>> response = notificationController.getNotifications(1L);

        verify(notificationService, times(1)).getNotificationsByUserId(1L);
        assert response.getBody().size() == 2;
    }

    @Test
    void testHealthCheck() {
        ResponseEntity<String> response = notificationController.healthCheck();
        assert response.getBody().equals("Notification Service is running");
    }
}
