package com.example.service;

import com.example.common.model.User;
import com.example.common.repository.UserRepository;
import com.example.common.utils.PasswordValidator;
import com.example.common.utils.ValidationUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(User user) {
        // 1. Normalization
        user.setEmail(ValidationUtils.normalizeEmail(user.getEmail()));
        user.setPhoneNumber(ValidationUtils.formatPhoneNumber(user.getPhoneNumber(), "BD")); // Default to BD per
                                                                                             // requirements

        // 2. Identity & Uniqueness (Active Users)
        validateUniqueness(user);

        // 3. Name Validation
        if (!ValidationUtils.isValidName(user.getFirstName()) || !ValidationUtils.isValidName(user.getLastName())) {
            throw new RuntimeException("Invalid First Name or Last Name format");
        }

        // 4. Age Policy (13+)
        if (!ValidationUtils.isOldEnough(user.getDateOfBirth(), 13)) {
            throw new RuntimeException("User must be at least 13 years old");
        }

        // 5. Password Policy
        String emailPrefix = user.getEmail().split("@")[0];
        String phoneSuffix = (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6)
                ? user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6)
                : null;
        PasswordValidator.validate(user.getPassword(), emailPrefix, phoneSuffix);

        // 6. Security: Hashing
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        // 7. Persistence
        userRepository.save(user);
    }

    private void validateUniqueness(User user) {
        Optional<User> existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            if (existingEmail.get().isDeleted()) {
                throw new RuntimeException(
                        "Registration blocked: User previously existed but was soft-deleted. Please contact admin.");
            }
            throw new RuntimeException("Email already exists");
        }

        if (user.getPhoneNumber() != null) {
            Optional<User> existingPhone = userRepository.findByPhoneNumber(user.getPhoneNumber());
            if (existingPhone.isPresent()) {
                if (existingPhone.get().isDeleted()) {
                    throw new RuntimeException(
                            "Registration blocked: User previously existed but was soft-deleted. Please contact admin.");
                }
                throw new RuntimeException("Phone number already exists");
            }
        }
    }
}
