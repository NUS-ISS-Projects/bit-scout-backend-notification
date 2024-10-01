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
    private final ObjectMapper objectMapper = new ObjectMapper(); // To deserialize Kafka message

    public WebSocketBroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(
        topics = "websocket-broadcasts",
        containerFactory = "websocketKafkaListenerContainerFactory"
    )
    public void consumeWebSocketBroadcast(String message) {
        try {
            System.out.println("Received WebSocket broadcast message: " + message);
            // Deserialize the message into NotificationDto
            NotificationDto notificationDto = objectMapper.readValue(message, NotificationDto.class);
            
            // Send the message to the specific user
            broadcastToWebSocketClients(notificationDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastToWebSocketClients(NotificationDto notificationDto) {
        String destination = "/topic/notifications/" + notificationDto.getUserId();
        System.out.println("Broadcasting WebSocket Notification to user " + notificationDto.getUserId() + " at destination "
                + destination);
        messagingTemplate.convertAndSend(destination, notificationDto);
        System.out.println("WebSocket Notification sent to user " + notificationDto.getUserId() + " at destination "
                + destination);
    }
}
