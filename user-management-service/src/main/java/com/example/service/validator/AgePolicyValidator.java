package com.example.service.validator;

import com.example.common.model.User;
import com.example.common.utils.ValidationUtils;

public class AgePolicyValidator implements UserValidator {
    private final int minAge;

    public AgePolicyValidator(int minAge) {
        this.minAge = minAge;
    }

    @Override
    public void validate(User user) {
        if (!ValidationUtils.isOldEnough(user.getDateOfBirth(), minAge)) {
            throw new RuntimeException("User must be at least " + minAge + " years old");
        }
    }
}
