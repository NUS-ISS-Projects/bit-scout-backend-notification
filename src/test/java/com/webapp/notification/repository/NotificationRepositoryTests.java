package com.webapp.notification.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.webapp.notification.entity.Notification;
import com.webapp.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class NotificationRepositoryTests {

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification notification;

    @BeforeEach
    public void setUp() {
        notification = new Notification();
        notification.setUserId("1L");
        notification.setToken("BTC");
        notification.setNotificationType("PriceAlert");
        notification.setNotificationValue(45000.0);
        notification.setRemarks("Test notification");
        notificationRepository.save(notification);
    }

    @Test
    public void testFindByUserId() {
        List<Notification> notifications = notificationRepository.findByUserId("1L");
        assertTrue(notifications.size() > 0);
        assertEquals("BTC", notifications.get(0).getToken());
    }

    @Test
    public void testFindByToken() {
        List<Notification> notifications = notificationRepository.findByToken("BTC");
        assertTrue(notifications.size() > 0);
        assertEquals("1L", notifications.get(0).getUserId());
    }
}
