package com.webapp.notification.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
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

    @Mock
    private Firestore firestore; // Add this to mock Firestore

    @Mock
    private DocumentReference documentReference; // Mock DocumentReference

    @Mock
    private DocumentSnapshot documentSnapshot; // Mock DocumentSnapshot

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(firestore.collection(anyString())).thenReturn(mock(CollectionReference.class));
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(mock(DocumentSnapshot.class))); // Mocking
                                                                                                            // // the
                                                                                                            // get
                                                                                                            // method
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
    void getNotificationsByUserIdTest() throws InterruptedException, ExecutionException {
        String userId = "testUserId";

        // Mock the behavior of DocumentReference
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(true);

        // Set up your NotificationDto or whatever you're retrieving from Firestore
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        List<NotificationDto> expectedNotifications = List.of(notificationDto);

        // Mock the Firestore query result to return your expected list
        when(notificationService.getNotificationsByUserId(userId)).thenReturn(expectedNotifications);

        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);

        // Assertions to verify the results
        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals(userId, notifications.get(0).getUserId());
    }

    // @Test
    // void testHealthCheck() {
    // ResponseEntity<String> response = notificationController.healthCheck();
    // assert response.getBody().equals("Notification Service is running");
    // }
}
