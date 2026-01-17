package com.example.service;

import com.example.common.model.User;
import com.example.common.repository.UserRepository;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }
        userRepository.save(user);
    }
}
