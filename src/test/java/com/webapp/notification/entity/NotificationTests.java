package com.webapp.notification.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificationTests {

    private Notification notification;

    @BeforeEach
    public void setUp() {
        notification = new Notification();
    }

    @Test
    public void testSetAndGetId() {
        Long id = 1L;
        notification.setId(id);
        assertEquals(id, notification.getId());
    }

    @Test
    public void testSetAndGetUserId() {
        Long userId = 123L;
        notification.setUserId(userId);
        assertEquals(userId, notification.getUserId());
    }

    @Test
    public void testSetAndGetToken() {
        String token = "BTC";
        notification.setToken(token);
        assertEquals(token, notification.getToken());
    }

    @Test
    public void testSetAndGetNotificationType() {
        String notificationType = "PriceAlert";
        notification.setNotificationType(notificationType);
        assertEquals(notificationType, notification.getNotificationType());
    }

    @Test
    public void testSetAndGetNotificationValue() {
        Double notificationValue = 50000.0;
        notification.setNotificationValue(notificationValue);
        assertEquals(notificationValue, notification.getNotificationValue());
    }

    @Test
    public void testSetAndGetRemarks() {
        String remarks = "Price crossed threshold";
        notification.setRemarks(remarks);
        assertEquals(remarks, notification.getRemarks());
    }
}
