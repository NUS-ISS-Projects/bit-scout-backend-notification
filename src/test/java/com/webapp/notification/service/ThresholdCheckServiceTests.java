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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Mock
    private RedisTemplate<String, List<Map<String, Object>>> redisTemplate;

    @Mock
    private ValueOperations<String, List<Map<String, Object>>> valueOperations;

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

        // Mock Redis behavior
        when(redisTemplate.opsForValue()).thenReturn(valueOperations); // Mock ValueOperations
        when(valueOperations.get(anyString())).thenReturn(null); // Simulate cache miss
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());
    }

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

    @Test
    void checkThresholdsForAllUsers_CacheMiss_ShouldFetchFromFirestore() throws InterruptedException, ExecutionException {
        // Simulate cache miss by returning null from Redis
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);

        // Simulate Firestore returning some documents
        QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(documentSnapshot));

        // Perform the check
        thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);

        // Verify Firestore was called
        verify(firestore.collection(anyString()), times(1)).get();
    }

    @Test
    void checkThresholdsForAllUsers_CacheHit_ShouldNotFetchFromFirestore() throws InterruptedException, ExecutionException {
        // Simulate cache hit by returning some cached data from Redis
        Map<String, Object> cachedData = mock(Map.class);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(Arrays.asList(cachedData));

        // Perform the check
        thresholdCheckService.checkThresholdsForAllUsers(priceUpdateDto);

        // Verify Firestore was not called
        verify(firestore.collection(anyString()), times(0)).get();
    }
}
