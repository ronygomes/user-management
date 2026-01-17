package com.example.service.validator;

import com.example.common.model.User;
import com.example.common.utils.ValidationUtils;

public class NameValidator implements UserValidator {
    @Override
    public void validate(User user) {
        if (!ValidationUtils.isValidName(user.getFirstName()) || !ValidationUtils.isValidName(user.getLastName())) {
            throw new RuntimeException("Invalid First Name or Last Name format");
        }
    }
}
