package com.webapp.notification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.webapp.notification.dto.NotificationDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class WebSocketKafkaConfig {


    // Create UUID-based group ID for WebSocket consumers, so that each consumer has a unique group ID, and each consumer receives all messages.
    // This is necessary because we want to broadcast messages to all WebSocket clients, not just the first pod that consumes the message (price-updates topic).
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationDto> websocketKafkaListenerContainerFactory(
        ConsumerFactory<String, NotificationDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, NotificationDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // Assign a unique group ID for WebSocket consumers
        factory.getContainerProperties().setGroupId("websocket-" + UUID.randomUUID().toString());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, NotificationDto> notificationDtoConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");  // Ensure all packages can be deserialized
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.webapp.notification.dto.NotificationDto"); // Specific to NotificationDto

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(NotificationDto.class));
    }
}
