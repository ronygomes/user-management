package me.ronygomes.userManagement.service;

import me.ronygomes.userManagement.common.dto.UserRegistrationDto;
import me.ronygomes.userManagement.common.dto.UserResponseDto;
import me.ronygomes.userManagement.common.dto.UserUpdateDto;

public interface UserService {

    UserResponseDto registerUser(UserRegistrationDto registrationDTO);

    UserResponseDto updateUser(String id, UserUpdateDto updateDTO);
}
