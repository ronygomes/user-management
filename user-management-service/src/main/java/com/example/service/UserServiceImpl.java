package com.example.service;

import com.example.common.model.User;
import com.example.common.repository.UserRepository;
import com.example.common.utils.ValidationUtils;
import com.example.service.validator.UserValidator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final List<UserValidator> validators;

    public UserServiceImpl(UserRepository userRepository, List<UserValidator> validators) {
        this.userRepository = userRepository;
        this.validators = validators;
    }

    @Override
    public void registerUser(User user) {
        // 1. Normalization (Structural/Format prep before business validation)
        user.setEmail(ValidationUtils.normalizeEmail(user.getEmail()));
        user.setPhoneNumber(ValidationUtils.formatPhoneNumber(user.getPhoneNumber(), "BD"));

        // 2. Business Validation (Strategy Pattern)
        validators.forEach(v -> v.validate(user));

        // 3. Security: Hashing
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        // 4. Persistence
        userRepository.save(user);
    }

    @Override
    public void updateUser(String id, User updatedUser) {
        // 1. Fetch existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Map allowed fields
        if (updatedUser.getDisplayName() != null)
            existingUser.setDisplayName(updatedUser.getDisplayName());
        if (updatedUser.getFirstName() != null)
            existingUser.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null)
            existingUser.setLastName(updatedUser.getLastName());
        if (updatedUser.getDateOfBirth() != null)
            existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        if (updatedUser.getPhoneNumber() != null)
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        if (updatedUser.getUsername() != null)
            existingUser.setUsername(updatedUser.getUsername());

        // Note: Password, Email, and IsDeleted are explicitly NOT updated here per
        // requirements

        // 3. Normalization logic
        existingUser.setPhoneNumber(ValidationUtils.formatPhoneNumber(existingUser.getPhoneNumber(), "BD"));

        // 4. Re-run business validation strategies
        validators.forEach(v -> v.validate(existingUser));

        // 5. Persistence
        userRepository.save(existingUser);
    }
}
