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
import java.util.Collections;
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
import com.google.cloud.firestore.WriteResult;
import com.webapp.notification.controller.NotificationController;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.repository.NotificationRepository;
import com.webapp.notification.service.NotificationService;
import com.webapp.notification.service.UserService;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import com.google.cloud.firestore.CollectionReference;

class NotificationControllerTests {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private CollectionReference collectionReference;

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

    @Mock
    private ApiFuture<DocumentSnapshot> apiFuture;

    @Mock
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // method
    }

    @Test
    public void testCreateOrUpdateNotification_NewDocument() throws InterruptedException, ExecutionException {
        // Arrange
        String userId = "user123";
        String token = "token123";

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setToken(token);
        notificationDto.setNotificationType("info");
        notificationDto.setNotificationValue(2000.0);
        notificationDto.setRemarks("Remarks");

        // Mock Firestore behavior
        CollectionReference mockCollection = mock(CollectionReference.class);
        DocumentReference mockDocument = mock(DocumentReference.class);
        ApiFuture<DocumentSnapshot> mockFuture = mock(ApiFuture.class);
        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        WriteResult mockWriteResult = mock(WriteResult.class);
        NotificationDto savedNotification = new NotificationDto(); // Mocked saved notification

        // Set up Firestore behavior
        when(firestore.collection("notifications")).thenReturn(mockCollection);
        when(mockCollection.document(userId)).thenReturn(mockDocument);
        when(mockDocument.get()).thenReturn(mockFuture);
        when(mockFuture.get()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(false); // New document scenario

        // Mock repository behavior
        when(notificationRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(mockDocument.set(any(Map.class))).thenReturn(mockFuture);
        when(mockWriteResult.getUpdateTime()).thenReturn(null); // Mock the update time

        // Act
        NotificationDto result = notificationService.createOrUpdateNotification(notificationDto);

        // Assert

    }

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
    // @Test
    // void getNotificationsByUserIdTest() throws ExecutionException,
    // InterruptedException {
    // String COLLECTION_NAME = "notifications";
    // // Mock the Firestore collection and document reference
    // CollectionReference collectionReference = mock(CollectionReference.class);
    // when(firestore.collection(COLLECTION_NAME)).thenReturn(collectionReference);

    // // Mock the document reference
    // DocumentReference documentReference = mock(DocumentReference.class);
    // when(collectionReference.document(anyString())).thenReturn(documentReference);

    // // Mock the API future and document snapshot
    // ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
    // when(documentReference.get()).thenReturn(future);

    // // Create a mock DocumentSnapshot and set up its behavior
    // DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
    // Map<String, Object> data = new HashMap<>();

    // // Mock notification data
    // Map<String, Object> notificationData = new HashMap<>();
    // notificationData.put("notificationType", "price");
    // notificationData.put("notificationValue", 100.0);
    // notificationData.put("remarks", "Price Alert");

    // // Add the notification data to the document data
    // data.put("btcjy", notificationData); // Use the token as key

    // // Set up DocumentSnapshot behavior
    // when(future.get()).thenReturn(documentSnapshot); // Make sure future.get()
    // returns documentSnapshot
    // when(documentSnapshot.exists()).thenReturn(true);
    // when(documentSnapshot.getData()).thenReturn(data);

    // // Call the method under test
    // List<NotificationDto> notifications =
    // notificationService.getNotificationsByUserId("userId");

    // // Assert the results
    // assertEquals(1, notifications.size()); // Ensure one notification is returned
    // NotificationDto notification = notifications.get(0);
    // assertEquals("userId", notification.getUserId());
    // assertEquals("btcjy", notification.getToken());
    // assertEquals("price", notification.getNotificationType());
    // assertEquals(100.0, notification.getNotificationValue());
    // assertEquals("Price Alert", notification.getRemarks());
    // }

    // }

    // @Test
    // void testHealthCheck() {
    // ResponseEntity<String> response = notificationController.healthCheck();
    // assert response.getBody().equals("Notification Service is running");
    // }
}