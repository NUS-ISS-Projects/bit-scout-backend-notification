package com.webapp.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.notification.dto.NotificationDto;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("!test") // Exclude this bean when the 'test' profile is active
public class WebSocketBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketBroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Notice the container factory in the listener to ensure the right configuration is used
    @KafkaListener(topics = "websocket-broadcasts", containerFactory = "kafkaListenerContainerFactory")
    public void consumeWebSocketBroadcast(NotificationDto notificationDto) {
        try {
            System.out.println("Received WebSocket broadcast: " + notificationDto);
            broadcastToWebSocketClients(notificationDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastToWebSocketClients(NotificationDto notificationDto) {
        String destination = "/topic/notifications/" + notificationDto.getUserId();
        messagingTemplate.convertAndSend(destination, notificationDto);
        System.out.println("WebSocket Notification sent to user " + notificationDto.getUserId());
    }
}

