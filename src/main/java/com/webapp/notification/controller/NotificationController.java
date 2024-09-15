package com.webapp.notification.controller;

import com.webapp.notification.dto.NotificationDto;
import com.webapp.notification.entity.Notification;
import com.webapp.notification.service.BinanceService;
import com.webapp.notification.service.BinanceWebSocketClient;
import com.webapp.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*") // Allow all origins
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BinanceService binanceService;

    private final BinanceWebSocketClient binanceWebSocketClient;

    public NotificationController(BinanceWebSocketClient binanceWebSocketClient) {
        this.binanceWebSocketClient = binanceWebSocketClient;
    }

    @PostMapping("/subscribe")
    public String subscribeCoins(@RequestBody List<String> coinPairs) {
        binanceWebSocketClient.subscribeToCoins(coinPairs);
        return "Subscribed to coins: " + coinPairs;
    }

    @PostMapping("/unsubscribe")
    public String unsubscribeCoins(@RequestBody List<String> coinPairs) {
        binanceWebSocketClient.unsubscribeFromCoins(coinPairs);
        return "Unsubscribed from coins: " + coinPairs;
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<String>> searchCoin(@RequestParam String symbol) {
        return binanceService.searchCoin(symbol)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // 3.3 Add Notification
    @PostMapping("/{userId}/add")
    public ResponseEntity<NotificationDto> addNotification(@PathVariable String userId,
            @RequestBody NotificationDto notificationDto) throws InterruptedException, ExecutionException {
        notificationDto.setUserId(userId);
        NotificationDto createdNotification = notificationService.createNotification(notificationDto);
        return ResponseEntity.ok(createdNotification);
    }

    // 3.4 Edit Notification
    @PutMapping("/{userId}/edit/{id}")
    public ResponseEntity<NotificationDto> editNotification(@PathVariable String userId, @PathVariable Long id,
            @RequestBody NotificationDto notificationDto) throws InterruptedException, ExecutionException {
        NotificationDto updatedNotification = notificationService.updateNotification(userId, id, notificationDto);
        return ResponseEntity.ok(updatedNotification);
    }

    // 3.5 Delete Notification
    @DeleteMapping("/{userId}/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String userId, @PathVariable Long id)
            throws InterruptedException, ExecutionException {
        notificationService.deleteNotification(userId, id);
        return ResponseEntity.noContent().build();
    }

    // Get all notifications for user
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable String userId)
            throws InterruptedException, ExecutionException {
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        String podName = System.getenv("HOSTNAME");
        return ResponseEntity.ok("Notification Service is up and running on pod: " + podName);
    }
}
