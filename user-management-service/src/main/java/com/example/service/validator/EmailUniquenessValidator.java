package com.example.service.validator;

import com.example.common.model.User;
import com.example.common.repository.UserRepository;

import java.util.Optional;

public class EmailUniquenessValidator implements UserValidator {
    private final UserRepository userRepository;

    public EmailUniquenessValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            if (existing.get().isDeleted()) {
                throw new RuntimeException(
                        "Registration blocked: User previously existed but was soft-deleted. Please contact admin.");
            }
            throw new RuntimeException("Email already exists");
        }
    }
}
