package com.webapp.notification.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificationDtoTests {

    private NotificationDto notificationDto;

    @BeforeEach
    public void setUp() {
        notificationDto = new NotificationDto();
    }

    @Test
    public void testSetAndGetUserId() {
        String userId = "test";
        notificationDto.setUserId(userId);
        assertEquals(userId, notificationDto.getUserId());
    }

    @Test
    public void testSetAndGetToken() {
        String token = "testToken";
        notificationDto.setToken(token);
        assertEquals(token, notificationDto.getToken());
    }

    @Test
    public void testSetAndGetNotificationType() {
        String type = "PriceUpdate";
        notificationDto.setNotificationType(type);
        assertEquals(type, notificationDto.getNotificationType());
    }

    @Test
    public void testSetAndGetNotificationValue() {
        Double value = 1500.0;
        notificationDto.setNotificationValue(value);
        assertEquals(value, notificationDto.getNotificationValue());
    }

    @Test
    public void testSetAndGetRemarks() {
        String remarks = "Threshold reached";
        notificationDto.setRemarks(remarks);
        assertEquals(remarks, notificationDto.getRemarks());
    }
}
