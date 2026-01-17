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
                // Skip validation if the existing user is the same as the one being validated
                if (user.getId() != null && user.getId().equals(existing.get().getId())) {
                    return;
                }

                if (existing.get().isDeleted()) {
                    throw new RuntimeException(
                            "Registration/Update blocked: User previously existed but was soft-deleted. Please contact admin.");
                }
                throw new RuntimeException("Phone number already exists");
            }
        }
    }
}
