package com.webapp.notification.config;

import com.webapp.notification.service.CryptoPriceWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notifications").setAllowedOrigins("*");
        registry.addEndpoint("/notifications").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(cryptoPriceWebSocketHandler(), "/ws/crypto-price").setAllowedOrigins("*");
    }

    @Bean
    public CryptoPriceWebSocketHandler cryptoPriceWebSocketHandler() {
        return new CryptoPriceWebSocketHandler();
    }


}

