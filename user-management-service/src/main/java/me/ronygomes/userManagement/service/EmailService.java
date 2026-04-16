package me.ronygomes.userManagement.service;

import me.ronygomes.userManagement.common.model.User;

public interface EmailService {

    void sendWelcomeEmail(User user);

}
