package com.webapp.notification.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.api.core.ApiFuture;
import com.webapp.notification.dto.PriceUpdateDto;
import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ThresholdCheckServiceTests {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ThresholdCheckService thresholdCheckService;

    private PriceUpdateDto priceUpdateDto;
    private Notification notification;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.openMocks(this);

        priceUpdateDto = new PriceUpdateDto();
        priceUpdateDto.setToken("BTC");
        priceUpdateDto.setPrice(60000.0);

        notification = new Notification();
        notification.setUserId("1L");
        notification.setToken("BTC");
        notification.setNotificationType("price fall to");
        notification.setNotificationValue(62000.0);

        // Mock Firestore behavior
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo(anyString(), anyString())).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
    }

    // @Test
    // void checkThresholdsForAllUsers_PriceFallReached_ShouldNotifyUser() throws InterruptedException, ExecutionException {
    //     // Set price below the notification threshold
    //     priceUpdateDto.setPrice(60000.0);  // Current price is below the threshold of 62000.0

    //     // Mock the Firestore response to return a valid notification
    //     QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
    //     when(documentSnapshot.toObject(Notification.class)).thenReturn(notification);
    //     when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(documentSnapshot));

    //     // Perform the check
    //     thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);

    //     // Verify that sendNotification() was called
    //     verify(notificationService, times(1)).sendNotification(any(NotificationDto.class));
    // }

    @Test
    void checkThresholdsForAllUsers_PriceRiseReached_ShouldNotNotifyUser() throws InterruptedException, ExecutionException {
        notification.setNotificationType("price rise to");
        notification.setNotificationValue(65000.0);

        QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
        when(documentSnapshot.toObject(Notification.class)).thenReturn(notification);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(documentSnapshot));

        thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);

        verify(notificationService, times(0)).sendNotification(any(NotificationDto.class));
    }

    @Test
    void checkThresholdsForAllUsers_NoNotifications_ShouldNotNotifyUser() throws InterruptedException, ExecutionException {
        // No documents should be returned
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList());

        thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);

        verify(notificationService, times(0)).sendNotification(any(NotificationDto.class));
    }
}
