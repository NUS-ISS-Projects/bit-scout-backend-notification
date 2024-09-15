package com.webapp.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BinanceService {

    private final WebClient webClient;

    public BinanceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.binance.com").build();
    }

    public Mono<String> searchCoin(String symbol) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/ticker/price")
                        .queryParam("symbol", symbol.toUpperCase())
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
