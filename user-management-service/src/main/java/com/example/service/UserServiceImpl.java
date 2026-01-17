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
}
