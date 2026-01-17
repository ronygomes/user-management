package com.example.service;

import com.example.common.dto.UserRegistrationDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.dto.UserUpdateDTO;

public interface UserService {

    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);

    UserResponseDTO updateUser(String id, UserUpdateDTO updateDTO);
}
