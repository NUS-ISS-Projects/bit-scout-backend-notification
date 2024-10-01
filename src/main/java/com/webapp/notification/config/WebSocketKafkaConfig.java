package com.webapp.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.UUID;

@Configuration
public class WebSocketKafkaConfig {

    @Bean
    // Create UUID-based group ID for WebSocket consumers, so that each consumer has a unique group ID, and each consumer receives all messages.
    // This is necessary because we want to broadcast messages to all WebSocket clients, not just the first pod that consumes the message (price-updates topic).
    public ConcurrentKafkaListenerContainerFactory<String, String> websocketKafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // Assign a unique group ID for WebSocket consumers
        factory.getContainerProperties().setGroupId("websocket-" + UUID.randomUUID().toString());
        return factory;
    }
}
