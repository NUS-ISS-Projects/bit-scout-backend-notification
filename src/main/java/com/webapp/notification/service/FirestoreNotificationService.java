package com.webapp.notification.service;

import com.webapp.notification.dto.NotificationDto;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FirestoreNotificationService {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "notifications";

    public NotificationDto createNotification(NotificationDto notificationDto)
        throws ExecutionException, InterruptedException {
        try {
            String userId = notificationDto.getUserId();
            CollectionReference notifications = firestore.collection(COLLECTION_NAME);
            DocumentReference document = notifications.document(userId);
            WriteResult result = document.set(notificationDto).get();
            return notificationDto; // Return the saved object or confirmation
        } catch (Exception e) {
            System.err.println("Error saving notification: " + e.getMessage());
            throw e;
        }
    }

    public NotificationDto updateNotification(String userId, NotificationDto notificationDto)
            throws ExecutionException, InterruptedException {
        // Update the notification
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId);

        WriteResult result = document.set(notificationDto).get();

        return notificationDto; // Return updated object or confirmation
    }

    public void deleteNotification(String userId) throws ExecutionException, InterruptedException {
        // Delete the notification
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId);

        document.delete().get();
    }

    public NotificationDto getNotification(String userId) throws ExecutionException, InterruptedException {
        // Retrieve the notification
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        DocumentReference document = notifications.document(userId);

        return document.get().get().toObject(NotificationDto.class);
    }

    public List<NotificationDto> getAllNotifications() throws ExecutionException, InterruptedException {
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);

        return notifications.get().get().getDocuments().stream()
                .map(doc -> doc.toObject(NotificationDto.class))
                .collect(Collectors.toList());
    }
}
