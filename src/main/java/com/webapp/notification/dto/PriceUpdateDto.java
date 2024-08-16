package com.webapp.notification.dto;

public class PriceUpdateDto {
    private String token;
    private double price;

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PriceUpdateDto{" +
                "token='" + token + '\'' +
                ", price=" + price +
                '}';
    }
}
