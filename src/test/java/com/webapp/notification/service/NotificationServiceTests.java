package com.webapp.notification.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;
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
import java.util.concurrent.ExecutionException;

class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.openMocks(this);

        notification = new Notification();
        notification.setId(1L);
        notification.setUserId("1L");
        notification.setToken("BTC");
        notification.setNotificationType("PriceAlert");
        notification.setNotificationValue(60000.0);
        notification.setRemarks("Test notification");

        notificationDto = new NotificationDto();
        notificationDto.setUserId("1L");
        notificationDto.setToken("BTC");
        notificationDto.setNotificationType("PriceAlert");
        notificationDto.setNotificationValue(60000.0);
        notificationDto.setRemarks("Test notification");

        // Mock Firestore behavior
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);

        // Mock Firestore write operations (set and delete)
        WriteResult writeResultMock = mock(WriteResult.class); // Mock the WriteResult
        when(documentReference.set(any())).thenReturn(writeResultFuture);
        when(documentReference.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResultMock); // Simulate successful get()

        // Mock Firestore read operations
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList()); // Mock empty result for getDocuments
    }

    // @Test
    // void createNotificationTest() throws InterruptedException, ExecutionException
    // {
    // // Mock repository save behavior
    // when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

    // // Call the method to test
    // NotificationDto result =
    // notificationService.createNotification(notificationDto);

    // // Assertions
    // assertNotNull(result);
    // assertEquals(notificationDto.getToken(), result.getToken());
    // verify(notificationRepository, times(1)).save(any(Notification.class));
    // verify(documentReference, times(1)).set(any());
    // }

    // @Test
    // void updateNotificationTest() throws InterruptedException, ExecutionException
    // {
    // // Mock repository find and save behavior
    // when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
    // when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

    // // Call the method to test
    // NotificationDto updatedDto = notificationService.updateNotification(1L, 1L,
    // notificationDto);

    // // Assertions
    // assertNotNull(updatedDto);
    // assertEquals(notificationDto.getRemarks(), updatedDto.getRemarks());
    // verify(notificationRepository, times(1)).save(any(Notification.class));
    // verify(documentReference, times(1)).set(any());
    // }

    // @Test
    // void deleteNotificationTest() throws InterruptedException, ExecutionException
    // {
    // // Mock repository delete behavior
    // doNothing().when(notificationRepository).deleteById(1L);

    // // Call the method to test
    // notificationService.deleteNotification("1L", "1L");

    // // Assertions
    // verify(notificationRepository, times(1)).deleteById(1L);
    // verify(documentReference, times(1)).delete();
    // }

    // @Test
    // void getNotificationsByUserIdTest() throws InterruptedException,
    // ExecutionException {
    // // Call the method to test
    // List<NotificationDto> notifications =
    // notificationService.getNotificationsByUserId("1L");

    // // Assertions
    // assertNotNull(notifications);
    // assertEquals(0, notifications.size()); // Based on the mocked empty result
    // verify(collectionReference, times(1)).get();
    // }

    @Test
    void sendNotificationTest() {
        // Mock WebSocket send behavior
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(NotificationDto.class));

        // Call the method to test
        notificationService.sendNotification(notificationDto);

        // Assertions
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(NotificationDto.class));
    }
}
