package com.example.service.validator;

import com.example.common.model.User;
import com.example.common.repository.UserRepository;

import java.util.Optional;

public class PhoneUniquenessValidator implements UserValidator {
    private final UserRepository userRepository;

    public PhoneUniquenessValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(User user) {
        if (user.getPhoneNumber() != null) {
            Optional<User> existing = userRepository.findByPhoneNumber(user.getPhoneNumber());
            if (existing.isPresent()) {
                if (existing.get().isDeleted()) {
                    throw new RuntimeException(
                            "Registration blocked: User previously existed but was soft-deleted. Please contact admin.");
                }
                throw new RuntimeException("Phone number already exists");
            }
        }
    }
}
