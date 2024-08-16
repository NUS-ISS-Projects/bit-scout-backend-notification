package com.webapp.notification.dto;

public class NotificationDto {
    private Long userId;
    private String token;
    private String notificationType;
    private Double notificationValue;
    private String remarks;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Double getNotificationValue() {
        return notificationValue;
    }

    public void setNotificationValue(Double notificationValue) {
        this.notificationValue = notificationValue;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
