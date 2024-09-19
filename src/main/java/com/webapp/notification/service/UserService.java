package com.webapp.notification.service;

public interface UserService {
    String validateTokenAndGetUserId(String token);
}
