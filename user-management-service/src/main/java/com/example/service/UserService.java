package com.example.service;

import com.example.common.model.User;

public interface UserService {
    void registerUser(User user);

    void updateUser(String id, User user);
}
