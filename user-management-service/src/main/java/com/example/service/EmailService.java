package com.example.service;

import com.example.common.model.User;

public interface EmailService {
    void sendWelcomeEmail(User user);
}
