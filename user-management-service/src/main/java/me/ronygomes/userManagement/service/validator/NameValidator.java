package me.ronygomes.userManagement.service.validator;

import me.ronygomes.userManagement.common.model.User;
import me.ronygomes.userManagement.common.utils.ValidationUtils;

public class NameValidator implements UserValidator {
    @Override
    public void validate(User user) {
        if (!ValidationUtils.isValidName(user.getFirstName()) || !ValidationUtils.isValidName(user.getLastName())) {
            throw new RuntimeException("Invalid First Name or Last Name format");
        }
    }
}
