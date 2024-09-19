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
        // Remove "Bearer " from the token if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove the "Bearer " part
        }
        
        String url = accountUrl + token;
        System.out.println("URL: " + url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        System.out.println("Response: " + response);

        if (response.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(response.getBody());
        } else {
            return null;
        }
    }
}