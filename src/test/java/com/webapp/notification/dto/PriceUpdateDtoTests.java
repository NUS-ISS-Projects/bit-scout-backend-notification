package com.webapp.notification.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PriceUpdateDtoTests {

    private PriceUpdateDto priceUpdateDto;

    @BeforeEach
    public void setUp() {
        priceUpdateDto = new PriceUpdateDto();
    }

    @Test
    public void testSetAndGetToken() {
        String token = "BTC";
        priceUpdateDto.setToken(token);
        assertEquals(token, priceUpdateDto.getToken());
    }

    @Test
    public void testSetAndGetPrice() {
        double price = 50000.0;
        priceUpdateDto.setPrice(price);
        assertEquals(price, priceUpdateDto.getPrice());
    }

    @Test
    public void testToString() {
        priceUpdateDto.setToken("ETH");
        priceUpdateDto.setPrice(4000.0);
        String expectedString = "PriceUpdateDto{token='ETH', price=4000.0}";
        assertEquals(expectedString, priceUpdateDto.toString());
    }
}
