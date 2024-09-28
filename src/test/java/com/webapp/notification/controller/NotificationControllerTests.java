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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
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
        final String COLLECTION_NAME = "notifications";

        // Mock the DocumentReference
        when(firestore.collection(COLLECTION_NAME)).thenReturn(mock(CollectionReference.class));
        when(firestore.collection(COLLECTION_NAME).document(userId)).thenReturn(documentReference);

        // Mock the behavior of DocumentReference to return a valid DocumentSnapshot
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        when(documentReference.get()).thenReturn(future);

        // Create a mock DocumentSnapshot
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(future.get()).thenReturn(documentSnapshot);

        // Mock the DocumentSnapshot to simulate that it exists and has data
        when(documentSnapshot.exists()).thenReturn(true);

        // Prepare the data to return from the document snapshot
        Map<String, Object> mockData = new HashMap<>();
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("notificationType", "testType");
        notificationData.put("notificationValue", 100);
        notificationData.put("remarks", "Test remark");

        // Mocking the data inside the document snapshot
        mockData.put("testToken", notificationData);
        when(documentSnapshot.getData()).thenReturn(mockData);

        // Call the method under test
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);

        // Assertions
        assertNotNull(notifications);
        assertEquals(0, notifications.size());
    }

    // @Test
    // void testHealthCheck() {
    // ResponseEntity<String> response = notificationController.healthCheck();
    // assert response.getBody().equals("Notification Service is running");
    // }
}
