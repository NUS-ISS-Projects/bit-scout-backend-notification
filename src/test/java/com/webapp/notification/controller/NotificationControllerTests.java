package com.webapp.notification.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.webapp.notification.controller.NotificationController;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.service.NotificationService;
import com.webapp.notification.service.UserService;

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

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // @Test
    // void testAddNotification() throws InterruptedException, ExecutionException {
    // NotificationDto notificationDto = new NotificationDto();
    // notificationDto.setToken("testToken");
    // notificationDto.setNotificationType("testType");

    // when(notificationService.createNotification(any(NotificationDto.class))).thenReturn(notificationDto);

    // ResponseEntity<NotificationDto> response =
    // notificationController.addNotification("test", notificationDto);

    // verify(notificationService,
    // times(1)).createNotification(any(NotificationDto.class));
    // assert response.getBody().getToken().equals("testToken");
    // }

    // @Test
    // void testEditNotification() throws InterruptedException, ExecutionException {
    // NotificationDto notificationDto = new NotificationDto();
    // notificationDto.setToken("updatedToken");

    // when(notificationService.updateNotification(eq("test"), eq("test"),
    // any(NotificationDto.class)))
    // .thenReturn(notificationDto);

    // ResponseEntity<NotificationDto> response =
    // notificationController.editNotification("test", "test", notificationDto);

    // verify(notificationService, times(1)).updateNotification(eq("test"),
    // eq("test"), any(NotificationDto.class));
    // assert response.getBody().getToken().equals("updatedToken");
    // }

    // @Test
    // void testDeleteNotification() throws InterruptedException, ExecutionException
    // {
    // ResponseEntity<Void> response =
    // notificationController.deleteNotification("test", "test");

    // verify(notificationService, times(1)).deleteNotification("test", "test");
    // assert response.getStatusCode().is2xxSuccessful();
    // }

    // @Test
    // void testGetNotifications() throws InterruptedException, ExecutionException {
    // NotificationDto notificationDto1 = new NotificationDto();
    // notificationDto1.setToken("testToken1");

    // NotificationDto notificationDto2 = new NotificationDto();
    // notificationDto2.setToken("testToken2");

    // List<NotificationDto> notifications = Arrays.asList(notificationDto1,
    // notificationDto2);

    // when(notificationService.getNotificationsByUserId("test")).thenReturn(notifications);

    // ResponseEntity<List<NotificationDto>> response =
    // notificationController.getNotifications("test");

    // verify(notificationService, times(1)).getNotificationsByUserId("test");
    // assert response.getBody().size() == 2;
    // }

    @Test
    void testGetNotifications() throws InterruptedException, ExecutionException {
        String token = "validToken";
        String userId = "testUserId";

        NotificationDto notificationDto1 = new NotificationDto();
        notificationDto1.setUserId(userId);

        NotificationDto notificationDto2 = new NotificationDto();
        notificationDto2.setUserId(userId);

        List<NotificationDto> notifications = Arrays.asList(notificationDto1, notificationDto2);

        when(userService.validateTokenAndGetUserId(token)).thenReturn(userId);
        when(notificationService.getNotificationsByUserId(userId)).thenReturn(notifications);

        ResponseEntity<List<NotificationDto>> response = notificationController.getNotifications(token);

        verify(userService, times(1)).validateTokenAndGetUserId(token);
        verify(notificationService, times(1)).getNotificationsByUserId(userId);

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    // @Test
    // void testHealthCheck() {
    // ResponseEntity<String> response = notificationController.healthCheck();
    // assert response.getBody().equals("Notification Service is running");
    // }
}
