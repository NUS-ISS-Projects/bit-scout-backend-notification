package com.webapp.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class BinanceWebSocketClient {

    private volatile WebSocketSession userSession = null;
    private final KafkaProducerService kafkaProducerService;
    private final Set<String> subscribedCoins = new CopyOnWriteArraySet<>();
    private static final String BINANCE_WS_ENDPOINT = "wss://stream.binance.com:9443/ws";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public BinanceWebSocketClient(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostConstruct
    public void connectToBinanceWebSocket() {
        String uri = BINANCE_WS_ENDPOINT;
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, new BinanceWebSocketHandler(), uri);
        manager.start();
    }

    public void subscribeToCoins(List<String> coinPairs) {
        subscribedCoins.addAll(coinPairs);
        sendSubscriptionMessage("SUBSCRIBE", coinPairs);
    }

    public void unsubscribeFromCoins(List<String> coinPairs) {
        subscribedCoins.removeAll(coinPairs);
        sendSubscriptionMessage("UNSUBSCRIBE", coinPairs);
    }

    private synchronized void sendSubscriptionMessage(String method, List<String> coinPairs) {
        if (userSession != null && userSession.isOpen()) {
            Map<String, Object> message = new HashMap<>();
            message.put("method", method);
            List<String> params = new ArrayList<>();
            for (String coin : coinPairs) {
                params.add(coin.toLowerCase() + "@trade");
            }
            message.put("params", params);
            message.put("id", System.currentTimeMillis());

            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                userSession.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                System.out.println("Error sending subscription message " + e);
            }
        } else {
            System.out.println("WebSocket session is not open.");
        }
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        if (userSession != null && userSession.isOpen()) {
            userSession.close();
        }
    }

    private class BinanceWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            userSession = session;
            System.out.println("Connected to Binance WebSocket");
            if (!subscribedCoins.isEmpty()) {
                sendSubscriptionMessage("SUBSCRIBE", new ArrayList<>(subscribedCoins));
            }
        }

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            kafkaProducerService.sendMessage(message.getPayload());
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            userSession = null;
            System.out.println("Disconnected from Binance WebSocket: {} " + status);
            // Optionally implement reconnection logic
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.out.println("WebSocket transport error" + exception);
        }
    }
}
