package com.webapp.notification.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.webapp.notification.service.UserService;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Value("${account.url}")
    private String accountUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String validateTokenAndGetUserId(String token) {
        String url = accountUrl + token;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(response.getBody());
        } else {
            return null;
        }
    }
}