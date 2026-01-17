package com.example.service.validator;

import com.example.common.model.User;
import com.example.common.utils.PasswordValidator;

public class PasswordPolicyValidator implements UserValidator {
    @Override
    public void validate(User user) {
        String emailPrefix = user.getEmail().split("@")[0];
        String phoneSuffix = (user.getPhoneNumber() != null && user.getPhoneNumber().length() >= 6)
                ? user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6)
                : null;
        PasswordValidator.validate(user.getPassword(), emailPrefix, phoneSuffix);
    }
}
