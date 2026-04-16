package me.ronygomes.userManagement.service.validator;

import me.ronygomes.userManagement.common.exception.ValidationException;
import me.ronygomes.userManagement.common.model.User;
import me.ronygomes.userManagement.common.utils.ValidationUtils;

public class AgePolicyValidator implements UserValidator {
    private final int minAge;

    public AgePolicyValidator(int minAge) {
        this.minAge = minAge;
    }

    @Override
    public void validate(User user) {
        if (!ValidationUtils.isOldEnough(user.getDateOfBirth(), minAge)) {
            throw new ValidationException("User must be at least " + minAge + " years old");
        }
    }
}
